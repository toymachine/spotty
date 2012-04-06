clean:
	find . -name "*.pyc" -exec rm -rf {} \;
	find . -name "*.pyo" -exec rm -rf {} \;
	find . -name "*~" -exec rm -rf {} \;
	find . -name ".#*" -exec rm -rf {} \;
	rm -rf war/WEB-INF/appengine-generated/
	rm -rf lib
	rm -rf build

etags:
	rm -rf TAGS
	find ./src -name "*.py" | etags --append --output TAGS -
	find ./test -name "*.py" | etags --append --output TAGS -
	find . -name "*.js" | etags --append --output TAGS -
	find . -name "*.coffee" | etags --append --output TAGS -
	find . -name "*.html" | etags --append --output TAGS -
	find . -name "*.css" | etags --append --output TAGS -
