import sys
import os
from fastapi import FastAPI, HTTPException, status
from pydantic import BaseModel
from typing import Optional
import logging
import requests
import json

# transformers_path = r"C:\Users\D_24\Documents\GitHub\CyberEDU\ml\CyberEDU\Lib\site-packages"
transformers_path = r"C:\Users\meow\Desktop\CyberEDU\ml\Lib\site-packages"
if transformers_path not in sys.path:
    sys.path.insert(0, transformers_path)

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(
    title="AI Chat API",
    description="API для чат-модели с Python 3.11.9",
    version="1.0.0"
)

class PromptRequest(BaseModel):
    prompt: str
    max_tokens: Optional[int] = 150
    temperature: Optional[float] = 0.7

class PromptResponse(BaseModel):
    status: str
    prompt: str
    response: str

tokenizer = None
model = None
model_loaded = False

def translate_google(text: str, target_lang: str = "en") -> str:
    try:
        if target_lang == "en":
            source_lang = "ru"
        else:
            source_lang = "en"
            target_lang = "ru"
        
        url = "https://translate.googleapis.com/translate_a/single"
        params = {
            'client': 'gtx',
            'sl': source_lang,
            'tl': target_lang,
            'dt': 't',
            'q': text
        }
        
        response = requests.get(url, params=params, timeout=10)
        response.raise_for_status()
        
        data = response.json()
        translated_text = data[0][0][0]
        
        logger.info(f"Перевод: '{text}' -> '{translated_text}'")
        return translated_text
        
    except Exception as e:
        logger.error(f"Ошибка перевода через Google: {e}")
        return text

def translate_libre(text: str, target_lang: str = "en") -> str:
    try:
        if target_lang == "en":
            source_lang = "ru"
        else:
            source_lang = "en"
            target_lang = "ru"
        
        endpoints = [
            "https://translate.argosopentech.com/translate",
            "https://libretranslate.de/translate",
        ]
        
        for endpoint in endpoints:
            try:
                payload = {
                    'q': text,
                    'source': source_lang,
                    'target': target_lang,
                    'format': 'text'
                }
                
                headers = {'Content-Type': 'application/json'}
                
                response = requests.post(
                    endpoint, 
                    data=json.dumps(payload), 
                    headers=headers, 
                    timeout=15
                )
                
                if response.status_code == 200:
                    data = response.json()
                    translated_text = data['translatedText']
                    logger.info(f"LibreTranslate: '{text}' -> '{translated_text}'")
                    return translated_text
                    
            except Exception as e:
                logger.warning(f"Ошибка с {endpoint}: {e}")
                continue
        
        return text
        
    except Exception as e:
        logger.error(f"Ошибка перевода через LibreTranslate: {e}")
        return text

def translate_text(text: str, target_lang: str = "en") -> str:
    if not text.strip():
        return text
    
    result = translate_google(text, target_lang)
    
    if result == text:
        result = translate_libre(text, target_lang)
    
    return result

@app.on_event("startup")
async def load_model():
    global tokenizer, model, model_loaded
    
    try:
        logger.info("Загрузка модели...")
        
        from transformers import AutoTokenizer, AutoModelForCausalLM
        import torch
        
        model_name = "openai-community/gpt2"
        
        logger.info(f"Загрузка модели {model_name}...")
        
        tokenizer = AutoTokenizer.from_pretrained(model_name)
        
        if tokenizer.pad_token is None:
            tokenizer.pad_token = tokenizer.eos_token
        
        model = AutoModelForCausalLM.from_pretrained(model_name)
        
        if torch.cuda.is_available():
            model = model.to('cuda')
            logger.info("Модель загружена на GPU")
        else:
            logger.info("Модель загружена на CPU")
        
        model_loaded = True
        logger.info(f"Модель {model_name} загружена успешно!")
        
    except Exception as e:
        logger.error(f"Ошибка загрузки модели: {e}")
        import traceback
        logger.error(traceback.format_exc())
        model_loaded = False

@app.post("/api")
async def handle_prompt(request: PromptRequest):
    if not model_loaded:
        raise HTTPException(
            status_code=500,
            detail="Модель не загружена. Проверьте логи сервера."
        )
    
    try:
        import torch
        
        original_prompt = request.prompt
        english_prompt = original_prompt
        
        russian_chars = any('а' <= char <= 'я' or 'А' <= char <= 'Я' for char in original_prompt)
        if russian_chars:
            logger.info(f"Перевод промта с русского на английский: {original_prompt}")
            english_prompt = translate_text(original_prompt, "en")
            logger.info(f"Переведенный промт: {english_prompt}")
        
        inputs = tokenizer.encode(
            english_prompt + tokenizer.eos_token, 
            return_tensors="pt"
        )
        
        if torch.cuda.is_available():
            inputs = inputs.to('cuda')
        
        with torch.no_grad():
            outputs = model.generate(
                inputs,
                max_new_tokens=request.max_tokens,
                temperature=request.temperature,
                do_sample=True if request.temperature > 0 else False,
                pad_token_id=tokenizer.eos_token_id,
                num_return_sequences=1
            )
        
        english_response = tokenizer.decode(outputs[0], skip_special_tokens=True)
        
        if english_response.startswith(english_prompt):
            english_response = english_response[len(english_prompt):].strip()
        
        english_response = english_response.replace(tokenizer.eos_token, '').strip()
        
        # Шаг 3: Перевод ответа обратно на русский
        final_response = english_response
        
        if english_response.strip() and russian_chars:
            logger.info(f"Перевод ответа с английского на русский: {english_response}")
            final_response = translate_text(english_response, "ru")
            logger.info(f"Переведенный ответ: {final_response}")
        
        return PromptResponse(
            status="success",
            prompt=original_prompt,
            response=final_response
        )
        
    except Exception as e:
        logger.error(f"Ошибка генерации: {str(e)}")
        import traceback
        logger.error(traceback.format_exc())
        raise HTTPException(
            status_code=500,
            detail=f"Ошибка генерации: {str(e)}"
        )

@app.get("/health")
async def health_check():
    return {
        "status": "healthy" if model_loaded else "model_not_loaded",
        "python_version": "3.11.9",
        "model_loaded": model_loaded
    }

@app.get("/test-translation")
async def test_translation():
    test_texts = [
        "Привет, как дела?",
        "Что ты умеешь?",
        "Расскажи о искусственном интеллекте"
    ]
    
    results = []
    for text in test_texts:
        en_translation = translate_text(text, "en")
        ru_back = translate_text(en_translation, "ru")
        
        results.append({
            "original": text,
            "english": en_translation,
            "russian_back": ru_back,
            "success": ru_back != text and ru_back != en_translation
        })
    
    return {"translation_tests": results}

@app.get("/test")
async def test_chat():
    if not model_loaded:
        return {"error": "Модель не загружена"}
    
    test_prompt = "Привет! Как дела?"
    
    try:
        import torch
        
        # Перевод на английский
        english_prompt = translate_text(test_prompt, "en")
        logger.info(f"Тестовый промт переведен на: {english_prompt}")
        
        inputs = tokenizer.encode(english_prompt + tokenizer.eos_token, return_tensors="pt")
        if torch.cuda.is_available():
            inputs = inputs.to('cuda')
        
        with torch.no_grad():
            outputs = model.generate(
                inputs,
                max_new_tokens=50,
                temperature=0.7,
                do_sample=True,
                pad_token_id=tokenizer.eos_token_id
            )
        
        english_response = tokenizer.decode(outputs[0], skip_special_tokens=True)
        if english_response.startswith(english_prompt):
            english_response = english_response[len(english_prompt):].strip()
        
        # Перевод обратно на русский
        russian_response = translate_text(english_response, "ru")
        
        return {
            "test_prompt": test_prompt,
            "english_prompt": english_prompt,
            "english_response": english_response,
            "russian_response": russian_response,
            "status": "success"
        }
        
    except Exception as e:
        return {"error": str(e)}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000, log_level="info")