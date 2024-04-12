# Charty

Maven is required to compile charty.

This project has only been tested on EndeavourOS Linux.


## Running the app
Use the terminal or Bring up the "Run Anything" menu(tap control twice) in intellij and type in
```sh
mvn clean javafx:run
```

## Generating API documentation
The generated docs will be present in api-docs/

```sh
mvn clean javafx:jlink javadoc:javadoc
```
