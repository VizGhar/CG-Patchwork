
# Git-Patchwork
This is multiplayer bot programming port of classical 2-player board game called Patchwork. You can find details about the game on [BoardGameGeek](https://boardgamegeek.com/boardgame/163412/patchwork) site. Or you can see the rules on [publisher's site](https://lookout-spiele.de/upload/en_patchwork.html_Rules_Patchwork_EN.pdf).

This sources falls under [WTF Public Licence](https://en.wikipedia.org/wiki/WTFPL) :) Do whatever you want with these sources. I'm author of every single asset used in this game. You can find them even on this [figma project](https://www.figma.com/file/7diUIxEWdBphhC6MSSp48s/Patchwork---Codingame?node-id=0:1).

Contribution page including game rules can be found on [this codingame site](https://www.codingame.com/contribute/view/30144011185fd6c9ffcdb35343cb35794acda). I don't feel any obligation to describe the game rules here.

## Brutaltester compatible
This game is [CG-Brutaltester](https://github.com/dreignier/cg-brutaltester) compatible. Brutaltester allows you to make multiple matches between 2 bots you have sources/executables to. Really good way to optimize parameters of your game. In order for you to try your bots with brutaltester follow these steps.
1. Make sure you have java-8 installed by running `java -version` (expect something like `1.8` or `8`) Brutaltester **doesn't work with Java 18** since some methods for reflections were deprecated/removed in newer versions of Java. I didn't dive into details.
2. Download brutaltester .jar from [CG-Brutaltester releases site](https://github.com/dreignier/cg-brutaltester/releases)
3. Download patchwork referee .jar from [CG-Patchwork releases site](https://github.com/VizGhar/CG-Patchwork/releases)
4. Run brutaltester with referee against your bots (you should know how to run your bot, adding examples below)

To see what the switches `-t`, `-n` etc. means please have a look at [CG-Brutaltester readme](https://github.com/dreignier/cg-brutaltester).

### Python brutaltester example:
Python is interpreted language. You can run your Python code using `python3 ./sources.py`. So to test `agent1.py` as reference against `agent2.py` as your new bot. Similar but not same will work for JavaScript, Ruby and other interpreted languages.
```
$ java -jar cg-brutaltester-1.0.0.jar -r "java -jar patchwork-1.0.jar" -p1 "python3 ./agent1.py" -p2 "python3 ./agent2.py" -t 4 -n 100
```

### C++ brutaltester example:
Compile sources using your favorite compiler, g++/gcc or similar to executable file. Let's say `agent1` for reference and `agent2` for your new bot.
```
$ g++ sources1.cpp -o ./agent1
$ g++ sources2.cpp -o ./agent2
```
```
$ java -jar cg-brutaltester-1.0.0.jar -r "java -jar patchwork-1.0.jar" -p1 "./agent1" -p2 "./agent2" -t 4 -n 4
```

### Java and Kotlin brutaltester example:
Compile source file similar to C++ example using javac/kotlinc or other jvm compiler
```
# Java -> agent1.jar
$ javac Player.java
$ jar cf agent1.jar Player.class

# Kotlin -> agent2.jar
$ kotlinc source.kt -include-runtime -d agent2.jar
```
Let them fight:
```
$ java -jar cg-brutaltester-1.0.0.jar -r "java -jar patchwork-1.0.jar" -p1 "java -jar ./agent1.jar" -p2 "java -jar ./agent2.jar" -t 4 -n 100
```

## Recommendations for newcomers
Recommendations for newcomers like I was some year back. This game:

1. has really huge search space (3 tiles with 8 different configurations = rotations and huge amount of free space in the beggining).
2. is not easy to simulate
3. can be simulated from start to end without any random external effects (just like chess, tic-tac-toe etc.)

To reach top this directly asks for Monte-Carlo class of algorithms or neural networks with really fast languages like Rust/C/C++. However if you won't be able to simulate whole game you can do your own heuristic in any language, or custom evaluation and try minimax/bfs/beam search. You can beat all wood level bosses by using heuristic since all of them are using one.

Just don't be afraid to ask anybody about how to do things. Best place to ask is [codingame discord](https://discord.com/invite/qAKXEEv).

## Prologue

I'm not asking you to support me. Just be nice and make best contributions you can to codingame. I'm really thankful for the codingame site and I've spent a few weeks to make this contribution possible. Have fun you guys.