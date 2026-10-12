# Third-Party Notices / Сторонние продукты

This file lists all third-party products, tools and standards that are used in
the build, development or operation of **Velocipenna**. The application's own
source code contains no copied third-party source files.

Этот файл содержит перечень всех сторонних продуктов, инструментов и стандартов,
которые используются при сборке, разработке или работе **Velocipenna**. Собственный
исходный код приложения не содержит скопированных сторонних исходников.

The project's own code is dedicated to the public domain under
[The Unlicense](LICENSE). This dedication does **not** extend to the
third-party products listed below — they remain under their own licenses and
copyrights. Their authors' rights are preserved here in accordance with those
licenses.

Собственный код проекта передан в общественное достояние (The Unlicense, см.
`LICENSE`). Это не распространяется на перечисленные ниже сторонние продукты —
они остаются под своими лицензиями и авторскими правами, информация о которых
приведена здесь для соблюдения этих лицензий.

---

## 1. Build & runtime dependencies (Maven)

### Apache Maven
- **Author/Copyright:** The Apache Software Foundation
- **License:** Apache License, Version 2.0
- **Notice:** Licensed products include the "Apache" trademark; attribution to
  The Apache Software Foundation is required. Not redistributed inside this
  repository — required externally to run `mvn clean package`.
- **URL:** https://maven.apache.org/ , https://www.apache.org/licenses/LICENSE-2.0

### maven-compiler-plugin
- **Author/Copyright:** The Apache Software Foundation
- **License:** Apache License, Version 2.0
- **URL:** https://maven.apache.org/plugins/maven-compiler-plugin/

### maven-shade-plugin
- **Author/Copyright:** The Apache Software Foundation
- **License:** Apache License, Version 2.0
- **URL:** https://maven.apache.org/plugins/maven-shade-plugin/

### JUnit 4 (test scope only, version 4.13.2)
- **Authors:** Kent Beck, David Saff, Andrew Eisenberg and contributors
- **Copyright:** Copyright (c) Dave Cleal, Kent Beck, John Asway, David Saff,
  Lisette Garcia and other contributors
- **License:** Eclipse Public License 1.0 (EPL-1.0)
- **Notice:** Used only for unit tests (`src/test/java`); it is NOT bundled into
  the shipped fat JAR (`jar:test-jar` is excluded from the shaded artifact).
- **URL:** https://junit.org/junit4/ , https://www.eclipse.org/legal/epl-v10.html

## 2. Development tools

### Eclipse IDE for Java Developers
- **Authors/Copyright:** Eclipse Foundation and contributors
- **License:** Eclipse Public License 2.0 (EPL-2.0)
- **Notice:** External development tool; not part of the distributed program.
- **URL:** https://www.eclipse.org/ , https://www.eclipse.org/legal/epl-2.0/

### JDK (Java Development Kit) / Java Runtime Environment (8+)
- **Copyright:** Oracle and/or its affiliates (OpenJDK builds: Oracle
  OpenJDK community contributors)
- **License:** GPLv2 + Classpath Exception (OpenJDK) / Oracle Binary Code License
  (Oracle JDK)
- **Notice:** Required platform to compile and run the application; not
  redistributed here.
- **URL:** https://openjdk.org/ , https://www.oracle.com/java/technologies/

## 3. Standards and formats referenced by the code (no code copied)

The writers for PDF/DOCX export (`PdfWriter.java`, `DocxWriter.java`) implement
published open standards from scratch, without copying any third-party source:

- **PDF 1.x specification** — © Adobe Systems Incorporated. The name "PDF" and
  the PDF logo are trademarks of Adobe. https://www.adobe.com/devnet/pdf/pdf_reference.html
- **Office Open XML (ECMA-376 / ISO/IEC 29500)** — Ecma International.
  https://ecma-international.org/publications-and-standards/standards/ecma-376/
- **ZIP (PKWARE APPNOTE)** — PKWARE, Inc. (implemented via the JDK's own
  `java.util.zip`).
- **"Helvetica" core font** — typeface design © Monotype Imaging Holdings Inc.;
  licensed from Bitstream Inc. for inclusion in PDF viewers per the PDF spec.
- **Character encodings Windows-1251 / UTF-8** — Microsoft (cp1251) and the
  Unicode Consortium (UTF-8), used as data standards via the JDK.

## 4. Bundled runtime libraries

**None.** The shaded fat JAR (`target/velocipenna.jar`) contains only this
project's own classes plus the JDK platform classes at runtime. All third-party
products above are external build/test tools or referenced standards, each
remaining the property of their respective authors under their own licenses.

---

If you believe any attribution here is missing or incorrect, please open an
issue in the repository: https://github.com/Bizonrelax/Velocipenna
