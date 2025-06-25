# Chatterly


Projekt składa się z dwóch modułów:

* **chatapp-server** – serwer aplikacji, obsługuje komunikację i logikę z wykorzystaniem bazy PostgreSQL oraz JWT
* **chatapp-client** – klient desktopowy napisany w JavaFX

---

## Wymagania wstępne

1. **Java JDK** (wersja 21+)
2. **Apache Maven** (wersja 3.9.10+)
3. **PostgreSQL** (wersja 15+)

---

## Konfiguracja bazy danych

1. Zainstaluj PostgreSQL zgodnie z dokumentacją: [https://www.postgresql.org/download/](https://www.postgresql.org/download/)
2. W terminalu lub w narzędziu pgAdmin utwórz nowego użytkownika i bazę danych:

   ```sql
   -- zaloguj się jako superuser (np. postgres):
   psql -U postgres

   -- utwórz użytkownika i przypisz hasło:
   CREATE USER chatapp_user WITH PASSWORD 'your_password';

   -- utwórz bazę danych:
   CREATE DATABASE chatapp_db OWNER chatapp_user;

   -- nadaj uprawnienia:
   GRANT ALL PRIVILEGES ON DATABASE chatapp_db TO chatapp_user;
   ```
3. Skopiuj plik `schema.sql` (znajdujący się w katalogu głównym projektu) i załaduj go do nowo utworzonej bazy:

   ```bash
   psql -U chatapp_user -d chatapp_db -f path/to/Chatterly/chatapp-server/db/schema.sql
   ```

---

## Ustawienia aplikacji

### 1. `chatapp-server`

1. Otwórz plik:

   ```
   chatapp-server/src/main/java/chatapp/server/db/Database.java
   ```
2. Zmień wartości stałych na dane utworzonej bazy:

   ```java
   public static final String URL      = "jdbc:postgresql://localhost:5432/chatapp_db";
   public static final String USER     = "chatapp_user";
   public static final String PASSWORD = "your_password";
   ```
3. Ustaw zmienną środowiskową `JWT_SECRET`, np. w systemach UNIX:

   ```bash
   export JWT_SECRET=YourSuperSecretKey
   ```

   Lub w Windows CMD:

   ```cmd
   set JWT_SECRET=YourSuperSecretKey
   ```

### 2. `chatapp-client`

Nie wymaga dodatkowych ustawień – po skonfigurowaniu serwera i bazy klient łączy się automatycznie.

---

## Uruchamianie aplikacji

1. **Serwer**

   ```bash
   cd chatapp-server
   mvn clean compile exec:java
   ```
2. **Klient**

   ```bash
   cd chatapp-client
   mvn clean javafx:run
   ```

---


