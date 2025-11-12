from fastapi import FastAPI, HTTPException, status
from pydantic import BaseModel
import requests
from typing import Optional


app = FastAPI(
    title="DeepSeek API",
    description="API для взаимодействия с DeepSeek",
    version="1.0.0"
)

DEEPSEEK_API_KEY = "sk-97963a0807fa47ac9323277c434a45e6"
DEEPSEEK_API_URL = "https://api.deepseek.com"

# Модели данных
class PromptRequest(BaseModel):
    prompt: str

class PromptResponse(BaseModel):
    response: str

class ErrorResponse(BaseModel):
    error: str

class HealthResponse(BaseModel):
    status: str

@app.post(
    "/api",
    response_model=PromptResponse,
    responses={
        400: {"model": ErrorResponse},
        500: {"model": ErrorResponse}
    },
    summary="Отправить промт в DeepSeek",
    description="Принимает промт и возвращает ответ от DeepSeek API"
)
async def handle_prompt(request: PromptRequest):
    try:
        deepseek_response = await send_to_deepseek(
            request.prompt
        )

        return PromptResponse(
            response=deepseek_response
        )
        
    except ValueError as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=str(e)
        )
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Произошла ошибка: {str(e)}"
        )

async def send_to_deepseek(prompt: str, max_tokens: int = 1000, temperature: float = 0.7):
    """
    Функция для отправки запроса в DeepSeek API
    """
    headers = {
        'Content-Type': 'application/json',
        'Authorization': f'Bearer {DEEPSEEK_API_KEY}'
    }
    
    payload = {
        'model': 'deepseek-chat',
        'messages': [
            {
                'role': 'user',
                'content': prompt
            }
        ],
        'max_tokens': 1000,
        'temperature': 0.7
    }
    
    try:
        response = requests.post(
            DEEPSEEK_API_URL,
            headers=headers,
            json=payload,
            timeout=30
        )
        
        response.raise_for_status()
        response_data = response.json()
        return response_data['choices'][0]['message']['content']
        
    except requests.exceptions.RequestException as e:
        raise Exception(f"Ошибка при обращении к DeepSeek API: {str(e)}")
    except (KeyError, IndexError) as e:
        raise Exception(f"Ошибка при обработке ответа от DeepSeek: {str(e)}")

@app.get(
    "/health",
    response_model=HealthResponse,
    summary="Проверка здоровья API",
    description="Эндпоинт для проверки работоспособности API"
)
async def health_check():
    return HealthResponse(status="API работает корректно")

@app.get("/", include_in_schema=False)
async def root():
    return {"message": "DeepSeek API Proxy работает!"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "main:app",
        host="localhost",
        port=8000,
        reload=True
    )