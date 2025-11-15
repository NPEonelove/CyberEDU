import sys
import os
from fastapi import FastAPI, HTTPException, status
from pydantic import BaseModel
from typing import Optional
import logging
import requests
import json

# transformers_path = r"C:\Users\D_24\Documents\GitHub\CyberEDU\ml\CyberEDU\Lib\site-packages"
# if transformers_path not in sys.path:
#     sys.path.insert(0, transformers_path)

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

class PromtResponseGenerateScenario(BaseModel):
    title: str
    text: str
    scam: str
    response: str


class PromptResponse(BaseModel):
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

# @app.on_event("startup")
# async def load_model():
#     global tokenizer, model, model_loaded
    
#     try:
#         logger.info("Загрузка модели...")
        
#         from transformers import AutoTokenizer, AutoModelForCausalLM
#         import torch
        
#         model_name = "openai-community/gpt2"
        
#         logger.info(f"Загрузка модели {model_name}...")
        
#         tokenizer = AutoTokenizer.from_pretrained(model_name)
        
#         if tokenizer.pad_token is None:
#             tokenizer.pad_token = tokenizer.eos_token
        
#         model = AutoModelForCausalLM.from_pretrained(model_name)
        
#         if torch.cuda.is_available():
#             model = model.to('cuda')
#             logger.info("Модель загружена на GPU")
#         else:
#             logger.info("Модель загружена на CPU")
        
#         model_loaded = True
#         logger.info(f"Модель {model_name} загружена успешно!")
        
#     except Exception as e:
#         logger.error(f"Ошибка загрузки модели: {e}")
#         import traceback
#         logger.error(traceback.format_exc())
#         model_loaded = False

# @app.post("/api")
# async def handle_prompt(request: PromptRequest):
#     if not model_loaded:
#         raise HTTPException(
#             status_code=500,
#             detail="Модель не загружена. Проверьте логи сервера."
#         )
    
#     try:
#         import torch
        
#         original_prompt = request.prompt
#         english_prompt = original_prompt
        
#         russian_chars = any('а' <= char <= 'я' or 'А' <= char <= 'Я' for char in original_prompt)
#         if russian_chars:
#             logger.info(f"Перевод промта с русского на английский: {original_prompt}")
#             english_prompt = translate_text(original_prompt, "en")
#             logger.info(f"Переведенный промт: {english_prompt}")
        
#         inputs = tokenizer.encode(
#             english_prompt + tokenizer.eos_token, 
#             return_tensors="pt"
#         )
        
#         if torch.cuda.is_available():
#             inputs = inputs.to('cuda')
        
#         with torch.no_grad():
#             outputs = model.generate(
#                 inputs,
#                 max_new_tokens=request.max_tokens,
#                 temperature=request.temperature,
#                 do_sample=True if request.temperature > 0 else False,
#                 pad_token_id=tokenizer.eos_token_id,
#                 num_return_sequences=1
#             )
        
#         english_response = tokenizer.decode(outputs[0], skip_special_tokens=True)
        
#         if english_response.startswith(english_prompt):
#             english_response = english_response[len(english_prompt):].strip()
        
#         english_response = english_response.replace(tokenizer.eos_token, '').strip()
        
#         # Шаг 3: Перевод ответа обратно на русский
#         final_response = english_response
        
#         if english_response.strip() and russian_chars:
#             logger.info(f"Перевод ответа с английского на русский: {english_response}")
#             final_response = translate_text(english_response, "ru")
#             logger.info(f"Переведенный ответ: {final_response}")
        
#         return PromptResponse(
#             response=final_response
#         )
        
#     except Exception as e:
#         logger.error(f"Ошибка генерации: {str(e)}")
#         import traceback
#         logger.error(traceback.format_exc())
#         raise HTTPException(
#             status_code=500,
#             detail=f"Ошибка генерации: {str(e)}"
#         )

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

# @app.get("/test")
# async def test_chat():
#     if not model_loaded:
#         return {"error": "Модель не загружена"}
    
#     test_prompt = "Привет! Как дела?"
    
#     try:
#         import torch
        
#         # Перевод на английский
#         english_prompt = translate_text(test_prompt, "en")
#         logger.info(f"Тестовый промт переведен на: {english_prompt}")
        
#         inputs = tokenizer.encode(english_prompt + tokenizer.eos_token, return_tensors="pt")
#         if torch.cuda.is_available():
#             inputs = inputs.to('cuda')
        
#         with torch.no_grad():
#             outputs = model.generate(
#                 inputs,
#                 max_new_tokens=50,
#                 temperature=0.7,
#                 do_sample=True,
#                 pad_token_id=tokenizer.eos_token_id
#             )
        
#         english_response = tokenizer.decode(outputs[0], skip_special_tokens=True)
#         if english_response.startswith(english_prompt):
#             english_response = english_response[len(english_prompt):].strip()
        
#         # Перевод обратно на русский
#         russian_response = translate_text(english_response, "ru")
        
#         return {
#             "test_prompt": test_prompt,
#             "english_prompt": english_prompt,
#             "english_response": english_response,
#             "russian_response": russian_response,
#             "status": "success"
#         }
        
#     except Exception as e:
#         return {"error": str(e)}

@app.post("/feedback")
async def get_feedback(request: PromptRequest):
    return PromptResponse(
        response="" \
        """
            Оценка: Идеально и абсолютно правильно.

Разбор по пунктам, почему это верное поведение:

Отказ от предоставления кода: Это главный и единственно правильный шаг. Код из СМС — это конфиденциальная информация, которая никогда и ни при каких обстоятельствах не должна передаваться третьим лицам. Настоящий сотрудник банка никогда не попросит вас продиктовать ему полный код из СМС. Мошенник использует прием "подтверждения личности", чтобы завладеть этим кодом и провести операцию, которую он якобы "блокирует".

Инициатива перезвонить самому: Это ключевой маневр, который полностью обезоруживает мошенника. Пользователь берет контроль над ситуацию в свои руки. Звонок на официальный, проверенный номер (не тот, с которого пришел звонок, и не тот, который может продиктовать мошенник!) — единственный способ проверить легитимность звонка.

Спокойный тон и завершение разговора: Мошенники часто используют давление и создают ощущение срочности, чтобы вы действовали импульсивно и не успели подумать. Вежливый, но твердый отказ и быстрое завершение звонка лишают их возможности манипулировать.

Распознание триггеров мошенников:

Имитация исходящего звонка из банка: Мошенники легко подделывают номер в определителе (спуфинг). Доверять нужно только исходящим звонкам от вас в банк.

Создание паники: История о списании крупной суммы в другом городе призвана вызвать шок и заставить вас действовать быстро.

Предложение "помощи": Мошенник позиционирует себя как "спаситель", который уже остановил операцию. Это вызывает доверие и желание помочь ему "завершить блокировку".

Что делать после такого звонка (дополнительные шаги для безопасности):

Немедленно перезвонить в банк: Набрать официальный номер службы поддержки Тинькофф (например, 8 800 555-77-78) с другого телефона или подождать несколько минут. Сообщить оператору о подозрительном звонке и проверить состояние счетов и карт.

Самостоятельно заблокировать карту: Если есть подозрения, карту можно временно заблокировать через мобильное приложение банка.

Ничего не нажимать и не переходить по ссылкам: Если параллельно со звонком приходят какие-либо СМС со ссылками, их игнорировать и не вводить свои данные.

Сообщить о мошенничестве: Можно сообщить о попытке мошенничества в банк, чтобы они могли предупредить других клиентов.

Вывод: В описанном сценарии пользователь повел себя абсолютно правильно, проявил бдительность и не поддался на манипуляции. Его действия являются эталоном безопасности при общении с незнакомцами по телефону на тему финансов.
        """
    )


@app.post("/explain-scenario")
async def get_explanation(request: PromptRequest):
    return PromptResponse(
        response="""
Тот звонок, который вы получили, — это не помощь от банка, а продуманный обман мошенников. Давайте разберем по шагам, что произошло и почему вы поступили абсолютно правильно.

1. Что это был за звонок на самом деле?
Это классическая схема мошенничества, которая называется «Vishing» (голосовой фишинг). Ее цель — завладеть вашими деньгами, обманом выманив у вас конфиденциальную информацию.

Мошенник не является сотрудником Тинькофф или любого другого банка. Он действует по отработанному сценарию, который строится на трех «китах»: страх, доверие, срочность.

2. Как работает обман? Разберем фразы мошенника:
«С вашей карты пытались списать деньги...»

Цель: Вызвать у вас шок и панику. Когда человек напуган, он хуже соображает и更容易 поддается манипуляциям.

Правда: Никакого списания, скорее всего, не было. Мошенник врет, чтобы создать проблему, которую он же потом «решит».

«Операция приостановлена, нужно ее заблокировать...»

Цель: Вызвать у вас доверие и чувство благодарности. Мошенник представляется вашим «спасителем». Логика жертвы проста: «Он же помогает мне, значит, он свой».

Правда: Настоящая служба безопасности банка никогда не будет согласовывать с вами блокировку по телефону. Они либо заблокируют подозрительную операцию автоматически, либо заблокируют карту, а потом уведомят вас."""
    )



@app.post("/generate-scenario")
async def generate_scenario(request: PromptRequest):
    if request.prompt.lower() == "фишинг":
        explanation_cache = """ПРОСТО ЗАКРОЙТЕ ЭТО ОКНО! Не нажимайте ни на какие кнопки в самом сообщении (даже «ОК» или «Отмена»). Просто закройте вкладку браузера или программу, в которой оно появилось. Если это всплывающее окно, закройте его через Диспетчер задач (Ctrl + Shift + Esc)."""
        return PromtResponseGenerateScenario(
            title="Уведомление от СберБанка",
            text="Уважаемый клиент! Во избежание полной блокировки интернет-банка «СберБанк Онлайн» необходимо в течение 2 часов подтвердить ваши данные. За последние 24 часа зафиксировано 3 попытки несанкционированного доступа к вашему счету из разных регионов. Для защиты средств перейдите по ссылке: sber-security.ru/verify и введите данные вашей карты. Игнорирование данного уведомления приведет к приостановке операций по счету.' as text",
            scam="true",
            response=explanation_cache)     
            
    if request.prompt.lower() == "техподдержка":
        return PromtResponseGenerateScenario(
            title="Срочное сообщение от Microsoft",
            text="Внимание! В вашей системе Windows 11 обнаружены критические уязвимости. Наш автоматический мониторинг зафиксировал активность вредоносного ПО типа Trojan.Win32. Для предотвращения утечки данных и повреждения системы требуется немедленная удаленная диагностика. Пожалуйста, перейдите на сайт microsoft-support.pro и скачайте приложение для удаленного доступа. Наш специалист поможет устранить угрозу. Операция займет не более 15 минут.",
            scam="true",
            response="""Правильный план действий (что нужно сделать НЕМЕДЛЕННО):
            ПРОСТО ЗАКРОЙТЕ ЭТО ОКНО!

            Не нажимайте ни на какие кнопки в самом сообщении (даже «ОК» или «Отмена»). Просто закройте вкладку браузера или программу, в которой оно появилось. Если это всплывающее окно, закройте его через Диспетчер задач (Ctrl + Shift + Esc).

            НИЧЕГО НЕ СКАЧИВАЙТЕ И НИКОМУ НЕ ЗВОНИТЕ.

            Самое главное: НЕ переходите на сайт microsoft-support.pro и НЕ скачивайте никаких приложений, особенно для удаленного доступа (таких как AnyDesk, TeamViewer, AmmyyAdmin и т.д.).

            Мошенники выдают себя за "специалистов", чтобы убедить вас установить программу, которая даст им полный контроль над вашим компьютером.

            НЕ ЗВОНИТЕ ПО УКАЗАННЫМ ТЕЛЕФОНАМ.

            Если в сообщении есть номер телефона, ни в коем случае не звоните по нему. Это мошеннический кол-центр, где с вами будут говорить профессиональные манипуляторы.

            ПРОВЕРЬТЕ КОМПЬЮТЕР САМОСТОЯТЕЛЬНО.

            Для собственного спокойствия вы можете самостоятельно запустить проверку на вирусы с помощью вашего установленного антивируса или встроенного в Windows Защитника Windows (Microsoft Defender).

            Откройте «Параметры Windows» (Пуск -> Шестеренка) -> «Обновление и безопасность» -> «Безопасность Windows» -> «Защита от вирусов и угроз». Запустите «Быструю проверку»."""
        )
    

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000, log_level="info")