### Предварительные требования

- **Python 3.11.9** (обязательно)
- GPU с поддержкой cuda121

Создайте виртуальное окружение:

# bash
python -m venv venv
Активируйте виртуальное окружение:

# bash
# Windows
venv\Scripts\activate

# Linux/macOS
source venv/bin/activate
Установите зависимости:

# bash
pip install -r requirements.txt
Запустите приложение:

# bash
python app.py
Приложение будет доступно по адресу: http://localhost:8000

API Endpoints
Основные endpoints
Метод	Endpoint	Описание
POST	/api	Основной чат-интерфейс
GET	/health	Проверка статуса сервера
POST	/feedback	Получение обратной связи по сценариям
POST	/generate-scenario	Генерация обучающих сценариев
POST	/explain-scenario	Объяснение сценариев мошенничества