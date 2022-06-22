MVN=mvn

default: exec

exec:
	$(MVN) javafx:run
doc:
	javadoc @gen-jvdoc --module-path $(JFX_HOME) --add-modules javafx.graphics,javafx.controls -d doc/
clean:
	$(MVN) clean
