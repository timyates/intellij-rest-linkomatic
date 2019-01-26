## Intellij REST link-o-matic

Adds a menu item to the context menu of the editor and the project view to copy a URL that can be opened in a browser, and it will open the file in intellij.

Caveats:

- IntelliJ has to be open when the other person clicks the link.
- The other person has to have the same project open
- The other person has to click an “Accept” link in a pop-up (unless they've turned off the trust security)

Useful for pasting links to a specific line into slack or otherwise...

### Installation:

- Download this repo, and run `./gradlew buildPlugin`
- Install `build/distributions/intellij-rest-linkomatic-1.0-SNAPSHOT.zip` as a plugin from file in the IntelliJ plugin manager.

### Works for

- Me
- IntelliJ Idea Ultimate 2018.3

Other versions might work too, but I've not tested it anywhere else...