import os
import argparse

def should_include_file(file_path, extensions, exclude_dirs):
    """Проверяет, нужно ли включать файл в результат"""
    # Проверяем расширение файла
    if extensions and not any(file_path.endswith(ext) for ext in extensions):
        return False
    
    # Проверяем, не находится ли файл в исключенной директории
    abs_path = os.path.abspath(file_path)
    for exclude_dir in exclude_dirs:
        if exclude_dir in abs_path:
            return False
    
    return True

def collect_code_files(root_dir, output_file, extensions=None, exclude_dirs=None):
    """
    Рекурсивно собирает содержимое файлов в указанной директории
    
    Args:
        root_dir (str): Корневая директория для поиска
        output_file (str): Имя выходного файла
        extensions (list): Список расширений файлов для включения (например ['.py', '.js', '.txt'])
        exclude_dirs (list): Список директорий для исключения
    """
    if extensions is None:
        extensions = ['.py', '.js', '.java', '.cpp', '.c', '.html', '.css', '.php', '.rb', '.go', '.rs', '.ts']
    
    if exclude_dirs is None:
        exclude_dirs = ['.git', '__pycache__', 'node_modules', 'venv', '.venv', 'env']
    
    # Добавляем полные пути для исключенных директорий
    exclude_dirs_full = [os.path.abspath(exclude_dir) for exclude_dir in exclude_dirs]
    
    with open(output_file, 'w', encoding='utf-8') as out_f:
        for root, dirs, files in os.walk(root_dir):
            # Исключаем директории из поиска
            dirs[:] = [d for d in dirs if os.path.abspath(os.path.join(root, d)) not in exclude_dirs_full]
            
            for file in files:
                file_path = os.path.join(root, file)
                
                if should_include_file(file_path, extensions, exclude_dirs_full):
                    try:
                        # Записываем разделитель с путем к файлу
                        out_f.write(f"\n{'='*80}\n")
                        out_f.write(f"ФАЙЛ: {file_path}\n")
                        out_f.write(f"{'='*80}\n\n")
                        
                        # Читаем и записываем содержимое файла
                        with open(file_path, 'r', encoding='utf-8') as in_f:
                            content = in_f.read()
                            out_f.write(content)
                            out_f.write('\n')  # Добавляем пустую строку в конце файла
                            
                    except UnicodeDecodeError:
                        # Пропускаем бинарные файлы
                        out_f.write(f"(бинарный файл или неподдерживаемая кодировка)\n\n")
                    except Exception as e:
                        out_f.write(f"(ошибка при чтении файла: {str(e)})\n\n")

def main():
    parser = argparse.ArgumentParser(description='Сборщик кода из всех файлов в директории')
    parser.add_argument('--output', '-o', default='all_code.txt', 
                       help='Имя выходного файла (по умолчанию: all_code.txt)')
    parser.add_argument('--dir', '-d', default='.', 
                       help='Корневая директория для поиска (по умолчанию: текущая директория)')
    parser.add_argument('--extensions', '-e', nargs='+', 
                       help='Расширения файлов для включения (например: .py .js .html)')
    parser.add_argument('--exclude-dirs', '-x', nargs='+',
                       help='Директории для исключения')
    
    args = parser.parse_args()
    
    # Запускаем сбор файлов
    collect_code_files(
        root_dir=args.dir,
        output_file=args.output,
        extensions=args.extensions,
        exclude_dirs=args.exclude_dirs
    )
    
    print(f"Все файлы собраны в: {args.output}")

if __name__ == "__main__":
    main()