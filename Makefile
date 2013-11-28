
OPTS:=-Dmaven.test.skip=true

skeeter:
	mvn scala:compile $(OPTS)
	mvn -q package $(OPTS)

docs:
	mvn -q site
	mvn -q scala:doc

clean:
	mvn -q clean

