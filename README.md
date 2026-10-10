# Velocipenna

A lightweight, high-performance local Java desktop application designed to inspect and analyze text files for Cyrillic characters. It serves as a specialized utility for uncovering mixed-script anomalies, character obfuscation, and homoglyph-based steganography.

## 1. Project Overview

Homoglyph steganography often capitalizes on visually identical characters across different alphabets (such as lookalike symbols in Latin and Cyrillic) to conceal hidden tracking tokens, bypass spam filters, or inject malicious obfuscated scripts. Velocipenna processes files locally, ensuring absolute data privacy while instantly identifying alphabet-mixing exploits.

## 2. Key Features

*   **Comprehensive Cyrillic Statistics:** Tracks and tallies all 33 Cyrillic letters, accurately distinguishing between upper-case and lower-case variants.
*   **Density Metrics:** Computes the exact mathematical ratio of Cyrillic characters relative to the total length of the text document.
*   **Word-Level Classification:** Filters and isolates words composed entirely of Cyrillic symbols versus those containing illicit mixed structures.
*   **Anomalous Word Extraction:** Pinpoints and extracts specific words that blend Cyrillic, Latin, or auxiliary symbol sets.
*   **State Preservation:** Automatically saves the window dimensions (width, height) and screen coordinates (X, Y positional values) upon application closure.
*   **Dynamic UI Scaling:** Features a localized font size spinner component that dynamically updates the visibility across all GUI labels and panels.

## 3. Architecture Principles

To maintain strict compliance with clean software development methodologies, Velocipenna completely rejects god-classes. The application is isolated into independent, decoupled packages:

1.  `ru.bizonrelax.detector.model` — Contains pure text analysis pipelines and character validation streams. Completely free from UI dependencies.
2.  `ru.bizonrelax.detector.view` — Manages the Swing layout infrastructure, modular panels, and text rendering.
3.  `ru.bizonrelax.detector.config` — Handles standalone I/O storage processes for user profile preferences and desktop windows properties.

## 4. Environment Requirements

*   **Operating System:** Microsoft Windows 11
*   **Development Kit:** Java Development Kit (JDK) 8
*   **Build Automation:** Apache Maven
*   **Integrated Environment:** Eclipse IDE for Java Developers (Version 2026-06 or newer)

## 5. License

This project is dedicated to the public domain under the terms of **The Unlicense**. You are completely free to copy, modify, publish, or distribute this software in both source and compiled forms for any purpose, commercial or non-commercial.











___
___
___




# Velocipenna

Легковесное и быстрое локальное десктопное приложение на Java, созданное для детального анализа текстовых файлов на предмет наличия кириллических символов. Утилита разработана специально для обнаружения скрытых аномалий смешения раскладок, маскировки текста и стеганографии на основе омоглифов.

## 1. Назначение программы

Стеганография с использованием омоглифов часто опирается на внешне неразличимые знаки из разных алфавитов (например, латинские и кириллические буквы «с», «е», «о», «р»). Это позволяет злоумышленникам прятать спам, обходить спам-фильтры или внедрять замаскированные скрипты. Velocipenna обрабатывает файлы строго локально на твоем компьютере, гарантируя конфиденциальность данных и мгновенно выявляя буквы-шпионы.

## 2. Основные возможности

*   **Полная статистика кириллицы:** Программа учитывает все 33 буквы русского алфавита, четко разделяя заглавные и строчные варианты.
*   **Метрика плотности:** Рассчитывает точный процент кириллических символов (КИС) от общего объема анализируемого текста.
*   **Классификация слов:** Разделяет слова, состоящие полностью из кириллицы, и слова, содержащие незаконные примеси из других алфавитов.
*   **Вывод аномалий:** Формирует список конкретных слов, в которых латиница, кириллица или спецсимволы оказались перемешаны в пределах одного слова.
*   **Сохранение состояния:** Автоматически запоминает размеры главного окна (ширину и высоту) и его положение на экране (координаты X и Y) при закрытии.
*   **Динамический масштаб шрифта:** Включает удобный спиннер для мгновенного изменения размера текста на всех элементах интерфейса.

## 3. Архитектура проекта

Чтобы избежать появления громоздких «классов-богов», архитектура Velocipenna разделена на три изолированных и слабо связанных пакета:

1.  `ru.bizonrelax.detector.model` — Чистая логика анализа текстовых потоков и валидации символов. Полностью изолирована от интерфейса.
2.  `ru.bizonrelax.detector.view` — Отвечает за графическую оболочку Swing, модульные панели и вывод результатов.
3.  `ru.bizonrelax.detector.config` — Автономный модуль для чтения и сохранения конфигурационных свойств (`.properties`) окна и профиля на диске.

## 4. Системные требования

*   **Операционная система:** Microsoft Windows 11
*   **Среда исполнения:** Java Development Kit (JDK) 8
*   **Сборщик проекта:** Apache Maven
*   **Инструмент разработки:** Eclipse IDE for Java Developers (версия 2026-06 или новее)

## 5. Лицензия

Этот проект передан в общественное достояние в рамках лицензии **The Unlicense**. Ты можешь свободно копировать, изменять, публиковать и распространять это программное обеспечение как в исходном, так и в скомпилированном виде для любых целей, включая коммерческие.
