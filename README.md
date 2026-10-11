# Velocipenna

**A lightweight, high-performance local Java desktop application that inspects and analyzes text files for Cyrillic characters.** It is a specialized utility for uncovering mixed-script anomalies, character obfuscation and homoglyph-based steganography — with a rich GUI, multi-format report export, five-language interface and full data privacy (everything runs 100% offline).

[Документация на русском языке ↓](#velocipenna-русский)

---

## English

### What it does

Homoglyph steganography exploits visually identical characters across different alphabets (Latin and Cyrillic look-alikes such as `c`, `e`, `o`, `p`, `x`, `a`, `T`) to hide tracking tokens, bypass spam filters or inject obfuscated scripts. Velocipenna scans any text file on your machine and instantly reports every place where alphabets are illegally mixed — no data ever leaves your computer.

### Key features

* **Full Cyrillic statistics** — tallies all 33 Russian letters separately for upper-case and lower-case variants, plus `Ё/ё`.
* **Density metric** — exact percentage of Cyrillic characters relative to the total document length.
* **Word-level classification** — separates pure-Cyrillic words from mixed-script anomalies and lists every suspicious word (up to 500 per report).
* **Charset auto-detection** — recognizes UTF-8 (with or without BOM) and Windows-1251 automatically; drag-and-drop a file onto the window to analyze it.
* **Multi-format export** — saves analysis reports as TXT, MD, HTML, XML, CSV, TSV, PDF and DOCX (PDF/DOCX generated natively, without external libraries; CSV/TSV open directly in MS Excel).
* **Long-term usage statistics** — launch count, total run time, analyzed characters, opened files and exported bytes survive restarts.
* **Persistent UI state** — window size, screen position and last-used directories are remembered between sessions.
* **Dynamic appearance** — background color via RGB spinners or the system palette with automatic font-contrast inversion, plus a font-size spinner that rescales the whole GUI.
* **Five-language interface** — Russian, English, German, Spanish and Chinese, switchable at runtime.
* **Extras** — clipboard export ("Буф"), in-GUI report editor, mouse-wheel scrolling in every window, an About dialog and a built-in Tic-Tac-Toe game against the computer.

### How it works

1. You open or drag-and-drop a text file (or paste clipboard text).
2. `CharsetDetector` probes the raw bytes and picks the right encoding (UTF-8 / CP1251).
3. `TextAnalyzer` + `WordTokenizer` stream through the document: counting every letter, measuring density and classifying each word as pure-Cyrillic or mixed-script.
4. The result (`AnalysisResult`) is rendered in the Swing GUI and can be exported in any of eight formats.
5. `ConfigStore` persists geometry, colors, fonts, paths and lifetime statistics to a `.properties` file next to the JAR.

### Architecture

To avoid god-classes, the application is split into independent, decoupled packages:

| Package | Responsibility |
|---|---|
| `ru.bizonrelax.detector.model` | Pure text-analysis logic: charset detection, tokenization, statistics, report rendering & export (TXT/MD/HTML/XML/CSV/TSV/PDF/DOCX). Zero Swing dependencies — fully unit-tested. |
| `ru.bizonrelax.detector.view` | Swing windows and dialogs: main window, extended "Доп" window, statistics/about/editor dialogs, tic-tac-toe, appearance utilities. |
| `ru.bizonrelax.detector.config` | Standalone persistence of user preferences and window state (`.properties`). |
| `ru.bizonrelax.detector.i18n` | Five-language dictionary (RU/EN/DE/ES/ZH), ASCII-safe source, RU fallback. |

### Requirements

* **OS:** Microsoft Windows 11 (any OS with a JVM will work)
* **JDK:** Java 8+
* **Build:** Apache Maven 3.6+
* **IDE (optional):** Eclipse IDE for Java Developers

### Build & run

```bat
:: Build the executable fat-JAR and run unit tests
mvn clean package

:: Launch the GUI (single jar, no extra files needed)
java -jar target\velocipenna.jar
```

Eclipse workflow: `File > Import > Maven > Existing Maven Projects` → select the repository root (`pom.xml`), then right-click `VelocipennaApp.java > Run As > Java Application`.

### License

Dedicated to the public domain under **The Unlicense**. Copy, modify, publish and distribute freely, commercial use included.

---

<a name="velocipenna-русский"></a>

# Velocipenna (русский)

**Легковесное и быстрое локальное десктопное приложение на Java для анализа текстовых файлов на наличие кириллических символов.** Утилита обнаруживает скрытые аномалии смешения раскладок, маскировку текста и стеганографию на основе омоглифов. Богатый графический интерфейс, экспорт отчётов в восемь форматов, пятиязычный интерфейс и полная конфиденциальность — всё работает 100 % офлайн.

### Для чего нужна

Стеганография с использованием омоглифов опирается на внешне неразличимые знаки из разных алфавитов (латинские и кириллические буквы «с», «е», «о», «р», «х», «а», «Т»). Это позволяет прятать треклинги, обходить спам-фильтры и внедрять замаскированные скрипты. Velocipenna обрабатывает файлы строго локально на вашем компьютере, гарантируя конфиденциальность данных и мгновенно выявляя буквы-шпионы.

### Основные возможности

* **Полная статистика кириллицы** — учёт всех 33 букв русского алфавита раздельно для заглавных и строчных вариантов, плюс «Ё/ё».
* **Метрика плотности** — точный процент кириллических символов от общего объёма текста.
* **Классификация слов** — разделение чисто кириллических слов и слов со «смешанным» алфавитом; вывод списка всех подозрительных слов (до 500 на отчёт).
* **Автоопределение кодировки** — распознавание UTF-8 (с BOM и без) и Windows-1251; файл можно просто перетащить мышью в окно программы.
* **Экспорт в 8 форматов** — TXT, MD, HTML, XML, CSV, TSV, PDF и DOCX (PDF/DOCX генерируются нативно, без сторонних библиотек; CSV/TSV открываются напрямую в MS Excel).
* **Нарастающая статистика использования** — число запусков, общее время работы, проанализированные символы, открытые файлы и выгруженные байты сохраняются между сеансами.
* **Сохранение состояния интерфейса** — размеры окна, координаты на экране и последние рабочие папки запоминаются после закрытия.
* **Динамическое оформление** — цвет фона через RGB-спиннеры или палитру системы с автоматическим инвертированием цвета шрифта для контраста; спиннер размера шрифта масштабирует весь интерфейс.
* **Пятиязычный интерфейс** — русский, английский, немецкий, испанский и китайский; переключение «на лету».
* **Дополнительно** — кнопка «Буф» (копирование отчёта в буфер обмена), встроенный редактор отчёта, прокрутка колесом мыши во всех окнах, диалог «О программе» и игра «Крестики-нолики» против компьютера.

### Как работает программа

1. Вы открываете файл через диалог или перетаскиваете его в окно (либо берёте текст из буфера обмена).
2. `CharsetDetector` анализирует сырые байты и выбирает кодировку (UTF-8 / CP1251).
3. `TextAnalyzer` и `WordTokenizer` проходят по документу: считают каждую букву, измеряют плотность и классифицируют каждое слово как чистое или смешанное.
4. Результат (`AnalysisResult`) отображается в интерфейсе Swing и экспортируется в любом из восьми форматов.
5. `ConfigStore` сохраняет геометрию окна, цвета, шрифты, пути и пожизненную статистику в `.properties`-файл рядом с JAR.

### Архитектура проекта

Чтобы избежать «классов-богов», приложение разделено на независимые слабо связанные пакеты:

| Пакет | Назначение |
|---|---|
| `ru.bizonrelax.detector.model` | Чистая логика анализа: определение кодировки, токенизация, статистика, формирование и экспорт отчётов (TXT/MD/HTML/XML/CSV/TSV/PDF/DOCX). Без зависимостей от Swing, покрыта юнит-тестами. |
| `ru.bizonrelax.detector.view` | Окна и диалоги Swing: главное окно, расширенное окно «Доп», диалоги статистики/«О программе»/редактора, крестики-нолики, утилиты оформления. |
| `ru.bizonrelax.detector.config` | Автономное сохранение пользовательских настроек и состояния окна в `.properties`. |
| `ru.bizonrelax.detector.i18n` | Словарь интерфейса на пяти языках (RU/EN/DE/ES/ZH), ASCII-безопасный исходник, откат на русский при отсутствии ключа. |

### Системные требования

* **ОС:** Microsoft Windows 11 (работает на любой системе с JVM)
* **Java:** JDK 8 или новее
* **Сборщик:** Apache Maven 3.6+
* **IDE (необязательно):** Eclipse IDE for Java Developers

### Сборка и запуск

```bat
:: Собрать исполняемый fat-JAR и прогнать юнит-тесты
mvn clean package

:: Запустить GUI (один файл, ничего дополнительно не нужно)
java -jar target\velocipenna.jar
```

Работа в Eclipse: `File > Import > Maven > Existing Maven Projects` → выберите корень репозитория (файл `pom.xml`), затем правый клик по `VelocipennaApp.java` → `Run As > Java Application`.

### Лицензия

Проект передан в общественное достояние в рамках **The Unlicense**. Свободное копирование, изменение, публикация и распространение — в исходном и компилированном виде, для любых целей, включая коммерческие.
