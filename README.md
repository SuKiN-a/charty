# Charty

Maven is required to compile charty.

This project has only been tested on EndeavourOS Linux.


## Running the app

```sh
mvn clean javafx:run
```

## Generating API documentation
The generated docs will be present in docs/charty/

```sh
mvn clean javafx:jlink javadoc:javadoc
```