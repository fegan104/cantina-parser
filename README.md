## Instructions

The easiest way to run the project is importing it into IntelliJ and running app.kt. From the IntelliJ run confiuration menu you can add command line arguments. If you would rather run it from the command line I've included an executable under the [zip file](/cantina-parser-1.0-SNAPSHOT.zip) in the project root. The project could also be built running `gradle distZip`.

Run the program by providing a list of selectors. For example:

`./cantina-parser StackView .column` 

The program then returns a list of all json objects that match all given selectors, and a count of how many objects matched. Based on a couple of test cases I included I believe my solution is correct.
