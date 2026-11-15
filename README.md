# CreamClient (Minecraft Forge 1.16.5)

Кастомный лаймовый клиент-мод для **Minecraft Forge 1.16.5** с тонким шрифтом (`DejaVuSans-ExtraLight`), моноширинным ASCII-баннером **CREAM** в главном меню, HUD-оверлеем и ClickGUI (`Right Shift`).

## Что внутри
- **Главное меню (`CreamMainMenuScreen`)**: тёмный фон с лаймовой сеткой, анимированный ASCII-баннер `CREAM` вместо стандартного логотипа Minecraft и кнопки в тонком стиле.
- **Тонкий шрифт**: встроенные TTF-шрифты `thin.json` (`DejaVuSans-ExtraLight.ttf`) и `mono.json` (`NotoSansMono-Regular.ttf`).
- **HUD и модули (`Right Shift`)**: Watermark + FPS, Coordinates, Keystrokes, ModuleList, ToggleSprint, Fullbright.

---

## Как собрать `.jar` и запустить

### Способ 1: Локально на Windows (нужен JDK 8)
1. Установите **Java 8 (JDK 8)** (например, Eclipse Temurin 8).
2. Распакуйте архив `CreamClient-1.16.5.zip`.
3. Если у вас установлен Gradle, откройте терминал в папке проекта и выполните:
   ```bash
   gradle build
   ```
   Или откройте `build.gradle` в **IntelliJ IDEA** (выберите `JDK 8` в Gradle JVM) и запустите задачу `Tasks -> build -> build` (или `Tasks -> forgegradle runs -> runClient` для мгновенного запуска клиента прямо из IDE).
4. Готовый файл появится в:
   `build/libs/CreamClient-1.16.5-1.0.0.jar`

### Способ 2: Через GitHub Actions (без установки JDK и Gradle на ПК)
1. Загрузите содержимое архива в новый репозиторий на GitHub (включая скрытую папку `.github/workflows/build.yml`).
2. Откройте вкладку **Actions** -> **Build CreamClient 1.16.5** -> нажмите **Run workflow** (или просто сделайте `git push`).
3. После завершения сборки скачайте готовый `.jar` из раздела **Artifacts** (`CreamClient-1.16.5`).

---

## Установка в Minecraft 1.16.5
1. Установите **Minecraft Forge 1.16.5** (рекомендуется версия `36.2.42` или любая `36.2.x`).
2. Перейдите в папку `.minecraft/mods` (на Windows: `Win + R` -> `%appdata%\.minecraft\mods`).
3. Поместите туда собранный файл `CreamClient-1.16.5-1.0.0.jar`.
4. Запустите профиль **Forge 1.16.5** в лаунчере.
5. В игре нажмите **Right Shift**, чтобы открыть **ClickGUI**.
