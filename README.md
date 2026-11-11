# QR Logbook Java desktop project

Two desktop interfaces are provided: Java Swing (`com.qrlogbook.Main`) and JavaFX (`com.qrlogbook.fx.FxApp`). They share local account/student storage and PBKDF2 password hashing.

## Build and launch Swing

Requires JDK 17+ and Maven:

```sh
mvn clean package
java -cp target/classes com.qrlogbook.Main
```

## Launch JavaFX

```sh
mvn javafx:run
```

The JavaFX controls dependency is declared in `pom.xml`; Maven resolves the native JavaFX artifacts for the current platform.

## Demo accounts

The first run seeds local demo accounts. The default credentials are:

| Role | ID | Password | Recovery email |
|---|---|---|---|
| Admin | `admin` | `adminpassword` | `admin@example.com` |
| Student | `02000422020` | `studentpassword` | `juan.delacruz@example.com` |
| Teacher | `TCH-001` | `teacherpassword` | `maria.santos@example.com` |

These are demo credentials, not suitable for real school accounts. The application stores local data in `~/.qr-logbook/accounts.properties`; this is a single-device demo, not a networked or centrally managed attendance system.

## Screens and behavior

- Role-select login with credential validation and show/hide password in Swing.
- Local account recovery by matching role, account ID, and saved recovery email, then setting a new password.
- Dashboard account counts and a student summary for student logins.
- Student records table with add, edit, delete, and duplicate-ID validation for admin/teacher roles.
- Swing and JavaFX share the same account data file and password-hashing implementation.

## Images

Copy the existing website image assets into `src/main/resources/assets/`, preserving the relative folders and names, for example:

```text
src/main/resources/assets/Classe365_qrcode-removebg-preview.png
src/main/resources/assets/7057_1566490658_9825_-removebg-preview.png
src/main/resources/assets/Assets/_Image_c53utdc53utdc53u.jpg
```

Both apps use bundled images when present and fall back to a purple background or simple logo mark when they are absent. JavaFX uses a styled text mark rather than image loading in its current screen implementation.
