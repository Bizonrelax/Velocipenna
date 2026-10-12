#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Generates Messages.java (5-language UI dictionary) with ASCII-only source."""
import io

RU = {
 "app.title": "Velocipenna — детектор кириллических омоглифов",
 "btn.open": "Открыть…", "btn.export": "Экспорт отчёта…",
 "btn.buffer": "Буф", "tip.buffer": "Скопировать в буфер обмена",
 "btn.about": "Оп", "tip.about": "О программе",
 "btn.extended": "Да_^", "tip.extended": "Открыть окно с расширенным функционалом",
 "lbl.font": "Шрифт:", "lbl.bgmode": "Фон:",
 "radio.spinner": "Спиннеры RGB", "radio.palette": "Палитра",
 "status.drop": "Перетащите .txt-файл сюда или нажмите \"Открыть…\"",
 "status.nothing": "Нечего экспортировать: сначала проанализируйте файл",
 "status.saved": "Отчёт сохранён: ", "status.saveerr": "Ошибка сохранения: ",
 "status.copied": "Отчёт скопирован в буфер обмена", "status.copyerr": "Не удалось скопировать: ",
 "report.file": "Файл: ", "report.charset": "Кодировка (определена): ",
 "report.total": "Всего символов: ", "report.cyrillic": "Кириллических символов: ",
 "report.density": "Плотность КИС: ", "report.pure": "Чистые кириллические слова: ",
 "report.mixed": "Слова со смешением график: ",
 "report.freq": "Частоты по буквам (верхний / нижний регистр):",
 "report.none": "Аномалии смешения график не обнаружены.",
 "report.found": "Найдено слов со смешением график:",
 "report.created": "Дата и время создания отчёта: ",
 "dlg.format": "Формат файла:",
 "ext.title": "Доп — расширенные функции",
 "ext.tip.textfont": "Размер шрифта вводимого текста", "ext.tip.uifont": "Размер шрифта элементов окна",
 "btn.servfiles": "СФа", "tip.servfiles": "Служебные файлы. Показать служебные файлы настроек и прочее",
 "btn.closeall": "ЗАв", "tip.closeall": "Закрыть всё. Закроются все окна. Останется лишь главное окно",
 "btn.savecfg": "Сох", "tip.savecfg": "Сохранить настройки",
 "btn.lang": "Яз", "tip.lang": "Смена языка интерфейса",
 "btn.editreport": "Ре", "tip.editreport": "Редактировать отчёт",
 "btn.stats": "Ст", "tip.stats": "Показать статистику",
 "ext.saved": "Настройки сохранены",
 "stats.title": "Статистика программы",
 "stats.firstlaunch": "Дата, время первого запуска: ", "stats.runtime": "Общее время работы программы: ",
 "stats.launches": "Количество запусков программы: ", "stats.chars": "Общее количество проанализированных символов: ",
 "stats.exports": "Общий размер экспортированных файлов: ", "stats.opened": "Общее количество открытых файлов: ",
 "stats.hours": " ч ", "stats.min": " мин ", "stats.sec": " с", "stats.mb": " МБ",
 "about.title": "О программе", "about.created": "Дата создания программы: ",
 "about.source": "Источник: GitHub, аккаунт Bizonrelax, репозиторий Velocipenna",
 "about.url": "Веб-адрес: https://github.com/Bizonrelax/Velocipenna",
 "about.desc": "Описание: локальное приложение для анализа текстов на кириллице и поиска смешения кириллицы с латиницей (омоглифов).",
 "about.usage": "Инструкция: откройте или перетащите .txt-файл; отчёт появится в окне; кнопки «Экспорт», «Буф» сохраняют и копируют отчёт; «Да_^» открывает расширенное окно.",
 "about.build": "Компиляция: нужен JDK 8+, Apache Maven 3.6+ и Eclipse IDE for Java Developers (https://www.eclipse.org/downloads/, https://maven.apache.org/download.cgi). Команда: mvn clean package, запуск: java -jar target/velocipenna.jar.",
 "about.thirdparty": "Сторонние продукты: Velocipenna использует внешние инструменты сборки (Apache Maven © The Apache Software Foundation, Apache License 2.0; JUnit 4 © Kent Beck, David Saff и соавторы, Eclipse Public License 1.0 — только для тестов; Eclipse IDE © Eclipse Foundation, EPL 2.0; JDK/OpenJDK © Oracle и сообщество OpenJDK) и реализует открытые стандарты PDF (© Adobe), Office Open XML (ECMA-376) и ZIP. Все они остаются под своими лицензиями; полный перечень авторов и лицензий — в файле THIRD-PARTY-NOTICES.md репозитория. Товарные знаки принадлежат их владельцам.",
}

EN = {
 "app.title": "Velocipenna — Cyrillic Homoglyph Detector",
 "btn.open": "Open…", "btn.export": "Export report…",
 "btn.buffer": "Buf", "tip.buffer": "Copy to clipboard",
 "btn.about": "Abt", "tip.about": "About the program",
 "btn.extended": "Ext_^", "tip.extended": "Open extended functionality window",
 "lbl.font": "Font:", "lbl.bgmode": "Background:",
 "radio.spinner": "RGB spinners", "radio.palette": "Palette",
 "status.drop": "Drop a .txt file here or press \"Open…\"",
 "status.nothing": "Nothing to export: analyze a file first",
 "status.saved": "Report saved: ", "status.saveerr": "Save error: ",
 "status.copied": "Report copied to clipboard", "status.copyerr": "Copy failed: ",
 "report.file": "File: ", "report.charset": "Charset (detected): ",
 "report.total": "Total characters: ", "report.cyrillic": "Cyrillic characters: ",
 "report.density": "Cyrillic density: ", "report.pure": "Pure Cyrillic words: ",
 "report.mixed": "Mixed-script words: ",
 "report.freq": "Letter frequencies (upper / lower):",
 "report.none": "Mixed-script anomalies: none detected.",
 "report.found": "Mixed-script words found:",
 "report.created": "Report created: ",
 "dlg.format": "File format:",
 "ext.title": "Ext — extended functions",
 "ext.tip.textfont": "Font size of typed text", "ext.tip.uifont": "Font size of window elements",
 "btn.servfiles": "Svc", "tip.servfiles": "Service files. Show settings files folder",
 "btn.closeall": "ClsAll", "tip.closeall": "Close all windows except the main one",
 "btn.savecfg": "Sav", "tip.savecfg": "Save settings",
 "btn.lang": "Lng", "tip.lang": "Switch interface language",
 "btn.editreport": "Edt", "tip.editreport": "Edit report",
 "btn.stats": "Sts", "tip.stats": "Show statistics",
 "ext.saved": "Settings saved",
 "stats.title": "Program statistics",
 "stats.firstlaunch": "First launch date/time: ", "stats.runtime": "Total running time: ",
 "stats.launches": "Launch count: ", "stats.chars": "Total analyzed characters: ",
 "stats.exports": "Total exported file size: ", "stats.opened": "Total opened files: ",
 "stats.hours": " h ", "stats.min": " min ", "stats.sec": " s", "stats.mb": " MB",
 "about.title": "About", "about.created": "Creation date: ",
 "about.source": "Source: GitHub, account Bizonrelax, repository Velocipenna",
 "about.url": "Web address: https://github.com/Bizonrelax/Velocipenna",
 "about.desc": "Description: local app analyzing Cyrillic texts and detecting mixed Cyrillic/Latin homoglyphs.",
 "about.usage": "Usage: open or drop a .txt file; the report appears in the window; Export/Buf buttons save and copy it; Ext_^ opens the extended window.",
 "about.build": "Build: JDK 8+, Apache Maven 3.6+, Eclipse IDE for Java Developers (https://www.eclipse.org/downloads/, https://maven.apache.org/download.cgi). Run: mvn clean package, then java -jar target/velocipenna.jar.",
 "about.thirdparty": "Third-party products: Velocipenna uses external build tools (Apache Maven (c) The Apache Software Foundation, Apache License 2.0; JUnit 4 (c) Kent Beck, David Saff et al., Eclipse Public License 1.0 - tests only; Eclipse IDE (c) Eclipse Foundation, EPL 2.0; JDK/OpenJDK (c) Oracle and the OpenJDK community) and implements open standards: PDF (c) Adobe, Office Open XML (ECMA-376) and ZIP. All remain under their own licenses; full author/license list in THIRD-PARTY-NOTICES.md in the repository. Trademarks belong to their respective owners.",
}

DE = {
 "app.title": "Velocipenna — Kyrillischer Homoglyph-Detektor",
 "btn.open": "Öffnen…", "btn.export": "Bericht exportieren…",
 "btn.buffer": "Zwf", "tip.buffer": "In die Zwischenablage kopieren",
 "btn.about": "Übg", "tip.about": "Über das Programm",
 "btn.extended": "Erw_^", "tip.extended": "Fenster mit erweiterten Funktionen öffnen",
 "lbl.font": "Schrift:", "lbl.bgmode": "Hintergrund:",
 "radio.spinner": "RGB-Zähler", "radio.palette": "Palette",
 "status.drop": ".txt-Datei hierher ziehen oder \"Öffnen…\" drücken",
 "status.nothing": "Nichts zu exportieren: zuerst eine Datei analysieren",
 "status.saved": "Bericht gespeichert: ", "status.saveerr": "Fehler beim Speichern: ",
 "status.copied": "Bericht in die Zwischenablage kopiert", "status.copyerr": "Kopieren fehlgeschlagen: ",
 "report.file": "Datei: ", "report.charset": "Zeichensatz (erkannt): ",
 "report.total": "Zeichen gesamt: ", "report.cyrillic": "Kyrillische Zeichen: ",
 "report.density": "Kyrill-Dichte: ", "report.pure": "Rein kyrillische Wörter: ",
 "report.mixed": "Wörter mit Schriftmischung: ",
 "report.freq": "Buchstabenhäufigkeit (groß / klein):",
 "report.none": "Keine Schriftmischungs-Anomalien erkannt.",
 "report.found": "Wörter mit Schriftmischung gefunden:",
 "report.created": "Bericht erstellt: ",
 "dlg.format": "Dateiformat:",
 "ext.title": "Erw — erweiterte Funktionen",
 "ext.tip.textfont": "Schriftgröße des eingegebenen Textes", "ext.tip.uifont": "Schriftgröße der Fensterelemente",
 "btn.servfiles": "DaD", "tip.servfiles": "Dienstdateien: Einstellungsordner anzeigen",
 "btn.closeall": "Alz", "tip.closeall": "Alle Fenster außer dem Hauptfenster schließen",
 "btn.savecfg": "Sp", "tip.savecfg": "Einstellungen speichern",
 "btn.lang": "Spr", "tip.lang": "Oberflächensprache wechseln",
 "btn.editreport": "Be", "tip.editreport": "Bericht bearbeiten",
 "btn.stats": "St", "tip.stats": "Statistik anzeigen",
 "ext.saved": "Einstellungen gespeichert",
 "stats.title": "Programmstatistik",
 "stats.firstlaunch": "Erster Start: ", "stats.runtime": "Gesamtlaufzeit: ",
 "stats.launches": "Anzahl Starts: ", "stats.chars": "Analysierte Zeichen gesamt: ",
 "stats.exports": "Exportierte Dateigröße gesamt: ", "stats.opened": "Geöffnete Dateien gesamt: ",
 "stats.hours": " Std ", "stats.min": " Min ", "stats.sec": " s", "stats.mb": " MB",
 "about.title": "Über das Programm", "about.created": "Erstellungsdatum: ",
 "about.source": "Quelle: GitHub, Konto Bizonrelax, Repository Velocipenna",
 "about.url": "Web-Adresse: https://github.com/Bizonrelax/Velocipenna",
 "about.desc": "Beschreibung: lokale Anwendung zur Analyse kyrillischer Texte und zum Auffinden gemischter kyrillisch/lateinischer Homoglyphen.",
 "about.usage": "Anleitung: .txt-Datei öffnen oder per Drag-and-drop; Bericht erscheint im Fenster; Export/Zwf speichern/kopieren; Erw_^ öffnet das Erweiterungsfenster.",
 "about.build": "Kompilierung: JDK 8+, Apache Maven 3.6+, Eclipse IDE for Java Developers (https://www.eclipse.org/downloads/, https://maven.apache.org/download.cgi). Befehl: mvn clean package, Start: java -jar target/velocipenna.jar.",
 "about.thirdparty": "Drittanbieterprodukte: Velocipenna nutzt externe Build-Werkzeuge (Apache Maven (c) The Apache Software Foundation, Apache License 2.0; JUnit 4 (c) Kent Beck, David Saff u. a., Eclipse Public License 1.0 - nur Tests; Eclipse IDE (c) Eclipse Foundation, EPL 2.0; JDK/OpenJDK (c) Oracle und die OpenJDK-Community) und setzt offene Standards um: PDF (c) Adobe, Office Open XML (ECMA-376) und ZIP. Alle bleiben unter ihren eigenen Lizenzen; vollstaendige Autoren-/Lizenzliste in THIRD-PARTY-NOTICES.md im Repository. Marken gehoeren ihren jeweiligen Eigentuemern.",
}

ES = {
 "app.title": "Velocipenna — Detector de homoglifos cirílicos",
 "btn.open": "Abrir…", "btn.export": "Exportar informe…",
 "btn.buffer": "Por", "tip.buffer": "Copiar al portapapeles",
 "btn.about": "Acer", "tip.about": "Acerca del programa",
 "btn.extended": "Ext_^", "tip.extended": "Abrir ventana con funciones extendidas",
 "lbl.font": "Fuente:", "lbl.bgmode": "Fondo:",
 "radio.spinner": "Contadores RGB", "radio.palette": "Paleta",
 "status.drop": "Arrastre un archivo .txt aquí o pulse \"Abrir…\"",
 "status.nothing": "Nada que exportar: analice un archivo primero",
 "status.saved": "Informe guardado: ", "status.saveerr": "Error al guardar: ",
 "status.copied": "Informe copiado al portapapeles", "status.copyerr": "Error al copiar: ",
 "report.file": "Archivo: ", "report.charset": "Codificación (detectada): ",
 "report.total": "Caracteres totales: ", "report.cyrillic": "Caracteres cirílicos: ",
 "report.density": "Densidad cirílica: ", "report.pure": "Palabras cirílicas puras: ",
 "report.mixed": "Palabras con mezcla de alfabetos: ",
 "report.freq": "Frecuencias por letra (mayúscula / minúscula):",
 "report.none": "No se detectaron anomalías de mezcla de alfabetos.",
 "report.found": "Palabras con mezcla de alfabetos encontradas:",
 "report.created": "Informe creado: ",
 "dlg.format": "Formato de archivo:",
 "ext.title": "Ext — funciones extendidas",
 "ext.tip.textfont": "Tamaño de fuente del texto escrito", "ext.tip.uifont": "Tamaño de fuente de los elementos",
 "btn.servfiles": "Ser", "tip.servfiles": "Archivos de servicio: mostrar carpeta de ajustes",
 "btn.closeall": "CloTo", "tip.closeall": "Cerrar todas las ventanas excepto la principal",
 "btn.savecfg": "Guar", "tip.savecfg": "Guardar ajustes",
 "btn.lang": "Idi", "tip.lang": "Cambiar idioma de la interfaz",
 "btn.editreport": "Edi", "tip.editreport": "Editar informe",
 "btn.stats": "Est", "tip.stats": "Mostrar estadísticas",
 "ext.saved": "Ajustes guardados",
 "stats.title": "Estadísticas del programa",
 "stats.firstlaunch": "Primera ejecución: ", "stats.runtime": "Tiempo total de ejecución: ",
 "stats.launches": "Número de ejecuciones: ", "stats.chars": "Caracteres analizados en total: ",
 "stats.exports": "Tamaño total exportado: ", "stats.opened": "Archivos abiertos en total: ",
 "stats.hours": " h ", "stats.min": " min ", "stats.sec": " s", "stats.mb": " MB",
 "about.title": "Acerca del programa", "about.created": "Fecha de creación: ",
 "about.source": "Fuente: GitHub, cuenta Bizonrelax, repositorio Velocipenna",
 "about.url": "Dirección web: https://github.com/Bizonrelax/Velocipenna",
 "about.desc": "Descripción: aplicación local para analizar textos cirílicos y detectar homoglifos mixtos cirílico/latino.",
 "about.usage": "Uso: abra o arrastre un archivo .txt; el informe aparece en la ventana; Exportar/Por guardan y copian; Ext_^ abre la ventana extendida.",
 "about.build": "Compilación: JDK 8+, Apache Maven 3.6+, Eclipse IDE for Java Developers (https://www.eclipse.org/downloads/, https://maven.apache.org/download.cgi). Comando: mvn clean package, ejecución: java -jar target/velocipenna.jar.",
 "about.thirdparty": "Productos de terceros: Velocipenna usa herramientas externas de compilacion (Apache Maven (c) The Apache Software Foundation, Apache License 2.0; JUnit 4 (c) Kent Beck, David Saff y otros, Eclipse Public License 1.0 - solo pruebas; Eclipse IDE (c) Eclipse Foundation, EPL 2.0; JDK/OpenJDK (c) Oracle y la comunidad OpenJDK) e implementa estandares abiertos: PDF (c) Adobe, Office Open XML (ECMA-376) y ZIP. Todos mantienen sus propias licencias; lista completa de autores y licencias en THIRD-PARTY-NOTICES.md en el repositorio. Las marcas registradas pertenecen a sus respectivos propietarios.",
}

ZH = {
 "app.title": "Velocipenna — 西里尔同形异码字符检测器",
 "btn.open": "打开…", "btn.export": "导出报告…",
 "btn.buffer": "复", "tip.buffer": "复制到剪贴板",
 "btn.about": "关", "tip.about": "关于程序",
 "btn.extended": "扩_^", "tip.extended": "打开扩展功能窗口",
 "lbl.font": "字体：", "lbl.bgmode": "背景：",
 "radio.spinner": "RGB 数值框", "radio.palette": "调色板",
 "status.drop": "将 .txt 文件拖到此处或点击“打开…”",
 "status.nothing": "没有可导出的内容：请先分析文件",
 "status.saved": "报告已保存： ", "status.saveerr": "保存错误： ",
 "status.copied": "报告已复制到剪贴板", "status.copyerr": "复制失败： ",
 "report.file": "文件： ", "report.charset": "编码（已识别）： ",
 "report.total": "总字符数： ", "report.cyrillic": "西里尔字符数： ",
 "report.density": "西里尔密度： ", "report.pure": "纯西里尔单词： ",
 "report.mixed": "混合字母文字： ",
 "report.freq": "字母频率（大写 / 小写）：",
 "report.none": "未检测到混合字母异常。",
 "report.found": "发现混合字母单词：",
 "report.created": "报告创建时间： ",
 "dlg.format": "文件格式：",
 "ext.title": "扩 — 扩展功能",
 "ext.tip.textfont": "输入文字的字体大小", "ext.tip.uifont": "窗口元素的字体大小",
 "btn.servfiles": "服", "tip.servfiles": "服务文件：显示设置文件夹",
 "btn.closeall": "关全", "tip.closeall": "关闭除主窗口外的所有窗口",
 "btn.savecfg": "存", "tip.savecfg": "保存设置",
 "btn.lang": "语", "tip.lang": "切换界面语言",
 "btn.editreport": "编", "tip.editreport": "编辑报告",
 "btn.stats": "统", "tip.stats": "显示统计",
 "ext.saved": "设置已保存",
 "stats.title": "程序统计",
 "stats.firstlaunch": "首次启动时间： ", "stats.runtime": "总运行时间： ",
 "stats.launches": "启动次数： ", "stats.chars": "累计分析字符数： ",
 "stats.exports": "累计导出文件大小： ", "stats.opened": "累计打开文件数： ",
 "stats.hours": " 小时 ", "stats.min": " 分钟 ", "stats.sec": " 秒", "stats.mb": " MB",
 "about.title": "关于程序", "about.created": "创建日期： ",
 "about.source": "来源：GitHub，账户 Bizonrelax，仓库 Velocipenna",
 "about.url": "网址：https://github.com/Bizonrelax/Velocipenna",
 "about.desc": "说明：本地应用，用于分析西里尔文本并检测西里尔/拉丁混合同形字符。",
 "about.usage": "使用：打开或拖入 .txt 文件；报告显示在窗口中；“导出/复”按钮保存和复制报告；“扩_^”打开扩展窗口。",
 "about.build": "编译：需要 JDK 8+、Apache Maven 3.6+、Eclipse IDE for Java Developers (https://www.eclipse.org/downloads/, https://maven.apache.org/download.cgi)。命令：mvn clean package，运行：java -jar target/velocipenna.jar。",
 "about.thirdparty": "第三方产品：Velocipenna 使用外部构建工具（Apache Maven © Apache 软件基金会，Apache License 2.0；JUnit 4 © Kent Beck、David Saff 等，Eclipse Public License 1.0 — 仅用于测试；Eclipse IDE © Eclipse 基金会，EPL 2.0；JDK/OpenJDK © Oracle 及 OpenJDK 社区）并实现开放标准：PDF (© Adobe)、Office Open XML (ECMA-376) 和 ZIP。它们均保持各自的原许可证；完整的作者与许可证列表见仓库中的 THIRD-PARTY-NOTICES.md 文件。商标归其各自所有者所有。",
}

LANGS = [("ru", RU), ("en", EN), ("de", DE), ("es", ES), ("zh", ZH)]
KEYS = sorted(RU.keys())

def esc(s):
    out = []
    for ch in s:
        o = ord(ch)
        if ch == '"':
            out.append('\\"')
        elif ch == '\\':
            out.append('\\\\')
        elif o < 128:
            out.append(ch)
        else:
            out.append('\\u%04X' % o)
    return ''.join(out)

lines = []
lines.append('package ru.bizonrelax.detector.i18n;')
lines.append('')
lines.append('import java.util.Collections;')
lines.append('import java.util.HashMap;')
lines.append('import java.util.LinkedHashMap;')
lines.append('import java.util.Locale;')
lines.append('import java.util.Map;')
lines.append('')
lines.append('/**')
lines.append(' * Five-language UI dictionary (TZ.md Dop-8): Russian, English, German,')
lines.append(' * Spanish, Chinese. Keys are ASCII identifiers; localized values use unicode')
lines.append(' * escapes so the source file stays ASCII-safe. Missing keys fall back to RU.')
lines.append(' */')
lines.append('public final class Messages {')
lines.append('')
lines.append('    public static final String[] LANG_CODES = {"ru", "en", "de", "es", "zh"};')
lines.append('    public static final String[] LANG_NAMES = {"\\u0420\\u0443\\u0441\\u0441\\u043a\\u0438\\u0439", "English", "Deutsch", "Espa\\u00F1ol", "\\u4E2D\\u6587"};')
lines.append('')
for code, d in LANGS:
    var = code.upper()
    lines.append('    private static final Map<String, String> %s = map(' % var)
    for k in KEYS:
        v = d.get(k, RU[k])
        lines.append('            "%s", "%s",' % (k, esc(v)))
    lines[-1] = lines[-1].rstrip(',')
    lines.append('    );')
    lines.append('')
lines.append('    private static final Map<String, Map<String, String>> ALL = new LinkedHashMap<String, Map<String, String>>();')
lines.append('    static {')
for code, _ in LANGS:
    lines.append('        ALL.put("%s", %s);' % (code, code.upper()))
lines.append('    }')
lines.append('')
lines.append('    private Messages() {')
lines.append('    }')
lines.append('')
lines.append('    public static String get(String lang, String key) {')
lines.append('        Map<String, String> dict = ALL.get(normalize(lang));')
lines.append('        String v = dict.get(key);')
lines.append('        if (v == null) v = RU.get(key);')
lines.append('        return v != null ? v : key;')
lines.append('    }')
lines.append('')
lines.append('    public static String normalize(String lang) {')
lines.append('        if (lang == null) return "ru";')
lines.append('        String l = lang.toLowerCase(Locale.ROOT);')
lines.append('        return ALL.containsKey(l) ? l : "ru";')
lines.append('    }')
lines.append('')
lines.append('    public static Map<String, String> dictionary(String lang) {')
lines.append('        return Collections.unmodifiableMap(ALL.get(normalize(lang)));')
lines.append('    }')
lines.append('')
lines.append('    private static Map<String, String> map(String... kv) {')
lines.append('        Map<String, String> m = new HashMap<String, String>();')
lines.append('        for (int i = 0; i + 1 < kv.length; i += 2) {')
lines.append('            m.put(kv[i], kv[i + 1]);')
lines.append('        }')
lines.append('        return m;')
lines.append('    }')
lines.append('}')

with io.open('src/main/java/ru/bizonrelax/detector/i18n/Messages.java', 'w', encoding='ascii') as f:
    f.write('\n'.join(lines) + '\n')
print("Messages.java written: %d lines, %d keys x %d langs" % (len(lines), len(KEYS), len(LANGS)))
