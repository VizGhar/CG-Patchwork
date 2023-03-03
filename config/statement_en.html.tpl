<!-- LEAGUES level1 level2 level3 level4 level5 -->
<div class="statement_back" id="statement_back" style="display:none"></div>
<div class="statement-body">

    <!-- BEGIN level1 -->
    <div style="color: #7cc576;
background-color: rgba(124, 197, 118,.1);
padding: 20px;
margin-right: 15px;
margin-left: 15px;
margin-bottom: 10px;
text-align: center;">
        <div style="margin-bottom: 6px">
            <img src="//cdn.codingame.com/smash-the-code/statement/league_wood_04.png"/>
        </div>

        <p style="font-weight: 700; margin-bottom: 6px;">
            This is a <b>league-based</b> game.
        </p>
        <div class="statement-league-alert-content">
            For this challenge, multiple leagues for the same game are available. Once you have proven yourself against the first Boss, you will access a higher league and extra rules will be available.
            <br><br>
            <p style="font-weight: 700; margin-bottom: 6px;">
                <b>Initial rules</b>
            </p>
            In this league, you will be introduced to the game of Patchwork. Now let's learn what the game turn looks like and how you pay for tetris-like patches using your time token.
        </div>
    </div>
    <!-- END -->

    <!-- BEGIN level2 -->
    <div style="color: #7cc576;
background-color: rgba(124, 197, 118,.1);
padding: 20px;
margin-right: 15px;
margin-left: 15px;
margin-bottom: 10px;
text-align: center;">
        <div style="margin-bottom: 6px">
            <img src="//cdn.codingame.com/smash-the-code/statement/league_wood_04.png"/>
        </div>

            <p style="font-weight: 700; margin-bottom: 6px;">
                <b>Patch Rotations</b>
            </p>
            Now that you can place patches on your quilt board and you understand how "time flies," you will be allowed to rotate and flip your patches.
        </div>
    <!-- END -->

    <!-- BEGIN level3 -->
    <div style="color: #7cc576;
background-color: rgba(124, 197, 118,.1);
padding: 20px;
margin-right: 15px;
margin-left: 15px;
margin-bottom: 10px;
text-align: center;">
        <div style="margin-bottom: 6px">
            <img src="//cdn.codingame.com/smash-the-code/statement/league_wood_04.png"/>
        </div>

            <p style="font-weight: 700; margin-bottom: 6px;">
                <b>Button Income</b>
            </p>
            Congratulations! Now you understand how to place your patches on a quilt board. Your button budget has now shrunk to a few buttons. So you will have to understand how to earn those shiny little buttons now.
        </div>
    <!-- END -->


    <!-- BEGIN level4 -->
    <div style="color: #7cc576;
background-color: rgba(124, 197, 118,.1);
padding: 20px;
margin-right: 15px;
margin-left: 15px;
margin-bottom: 10px;
text-align: center;">
        <div style="margin-bottom: 6px">
            <img src="//cdn.codingame.com/smash-the-code/statement/league_wood_04.png"/>
        </div>

            <p style="font-weight: 700; margin-bottom: 6px;">
                <b>Bonus button and Special Patches</b>
            </p>
            The last step is to understand bonuses and how they affect the game. 2 new terms are introduced in this league. A <strong>Bonus Button</strong> gives you 7 bonus points towards your final score. You will receive it for filling in a 7x7 square. You can also earn <strong>Special Patch</strong> when crossing certain time points on a Timeline.
        </div>
    <!-- END -->

    <!-- GOAL -->
    <div class="statement-section statement-goal">
        <h1>
            <span class="icon icon-goal">&nbsp;</span>
            <span>The Goal</span>
        </h1>
        <div class="statement-goal-content">
Become the master of patchworking by filling your quilt canvas with patches. Be careful! Every patch costs you a few <strong>Buttons</strong>, and it also takes some <strong>Time</strong> to sew it on your canvas. Those are your most valuable resources.<br><br>
This game is a port of the 2 player board game <a
                href="https://boardgamegeek.com/boardgame/163412/patchwork">Patchwork</a>.<br/><br/>
My thanks to <a href="https://www.codingame.com/profile/b3168ed8b0bc58c683ae18284d2087e21969904">Butanium</a> for his help with the custom interaction module (zooming to patches on mouse hover).
        </div>
    </div>

    <!-- RULES -->
    <div class="statement-section statement-rules">
        <h1>
            <span class="icon icon-rules">&nbsp;</span>
            <span>Rules</span>
        </h1>
        <div>
            <div class="statement-rules-content">
                This is a turn-based game, but players do not necessarily alternate between turns. The player furthest behind on the <strong>Timeline</strong> takes his turn. This may result in a player taking multiple turns in a row before his opponent can take one. If there are both <strong>Time tokens</strong> on the same spot, the player that played last takes his turn.
                <br><br>
                <img src="https://cdn-games.codingame.com/community/3996809-1661712962734/040aa5fb711fa0bef0af0c3a43311ded84efe973f3622aac60f5c5c66b8d8469.png"/>
                <br><br>
The game starts with a pre-shuffled deck of <const>33</const> patches. All patches are placed on the table and are visible to both players.
<br><br>
Your goal is to collect more <strong>Buttons</strong> than your opponent and to cover as much of your quilt board with patches as possible. Uncovered squares on the quilt board will cost you <const>2</const> <strong>Buttons</strong> per piece at the end of the game.
<br><br>
On your turn, you carry out one of the following actions:
<ul>
    <li>
Advance and Receive Buttons using <const>SKIP</const> command
    </li>
    <li>
Take and Place a Patch using <const>PLAY</const> command
    </li>
</ul>
If you use <const>PLAY</const> incorrectly, <const>SKIP</const> will be used instead.

                    <!-- BEGIN level4 level5 -->
                    <br/>
                    <!-- BEGIN level4 -->
                                    <div class="statement-new-league-rule">
<!-- END -->

If you use <const>SKIP</const> while playing <strong>Special Patch</strong>, the patch will be placed at the first available position.
                                        <!-- BEGIN level4 -->
                                    </div>
<!-- END -->
                    <!-- END -->
            </div>
        </div>
        <br>
        <h1>
            <span>Advance and Receive Buttons - the <const>SKIP</const> command</span>
        </h1>
        <div class="statement-rules-content">
            Your <strong>Time token</strong> will move on the <strong>Timeline</strong> so that it occupies the space
            directly in front of your
            opponent's <strong>Time token</strong>.
            <!-- BEGIN level3 level4 level5 -->
            <br/><br/>

                <!-- BEGIN level3 -->
                <div class="statement-new-league-rule">
                <!-- END -->
            You receive <const>1</const> <strong>Button</strong> per Timepoint your time token moves.
                <!-- BEGIN level3 -->
                </div>
                <!-- END -->
            <!-- END -->
        </div>
        <br>
        <h1>
            <span>Take and Place a Patch - the <const>PLAY</const> command</span>
        </h1>
        <div class="statement-rules-content">
            This action consists of a few steps:
            <ul>
                <li>Choose from the available <strong>Patches</strong> and place it on your quilt board</li>
                <li>Pay the depicted number of <strong>Buttons</strong> to the supply</li>
                <li>Move your <strong>Time token</strong> on the time board by a number of spaces as depicted on the label.
                </li>
            </ul>
            
            <!-- BEGIN level1 -->
            A patch can be placed anywhere on the quilt board as long as it won't cover already placed patches.
            <!-- END -->

            <!-- BEGIN level2 level3 level4 level5 -->

                <!-- BEGIN level2 -->
                <div class="statement-new-league-rule">
                <!-- END -->

            <strong>Patches</strong> can be rotated in any way - flipped (horizontally) and/or rotated right (clockwise)
            <const>0</const>
            ,
            <const>1</const>
            ,
            <const>2</const>
            , or
            <const>3</const>
            times.
            
            A patch can be placed anywhere on the quilt board as long as it won't cover already placed patches.
            
            <br/><br/>
            Flipping Patch:
            <br/>
            <br/>
            <img alt="Patch flip"
                 src="https://cdn-games.codingame.com/community/3996809-1661712962734/e42b794a5de5ba64c069cc2937f66edfddcbc7770c39586ad408bbe7a5fe2a61.png">

            <br/><br/>
            Rotating Patch:
            <br/>
            <br/>
            <img alt="Patch rotation"
                 src="https://cdn-games.codingame.com/community/3996809-1661712962734/e80af6091ecc6d81b0c6ddc70ce73ffbbad5354fc87ea36ddc1405bd54ad0883.png">

                <!-- BEGIN level2 -->
                </div>
                <!-- END -->
                 <!-- END -->
        </div>
        <br>
        <h1>
            <span>Timeline</span>
        </h1>
        <div class="statement-rules-content">
            The <strong>Timeline</strong> consists of a starting point and
            <!-- BEGIN level1 -->
            <const>19</const>
            <!-- END -->
            <!-- BEGIN level2 level3 level4 level5 -->
            <const>53</const>
            <!-- END -->
            time points. Regardless of the action you take, you always move your time token on the Timeline.
            <br/><br/>
            <!-- BEGIN level3 -->
                <div class="statement-new-league-rule">
            Some points in the Timeline are marked with the <strong>Button Income</strong> icon. Whenever you move onto or past such point, you receive a number of buttons according to the patches on your quilt board.
                </div>
            <br/>

            <img src="https://cdn-games.codingame.com/community/3996809-1661712962734/b83c8423a27bc1072c5bc9bbf6a7358f871b7a215ba6656d3dd5c9518c4975c1.png"/>
            
            <!-- END -->


            <!-- BEGIN level4 level5 -->
            <!-- BEGIN level4 -->
                <div class="statement-new-league-rule">
                    <!-- END -->
            Some points in the Timeline are marked with the <strong>Button Income</strong> or <strong>Special Patch</strong> icon. Whenever you move onto or past one of those spaces, resolve the corresponding event:
            <ul>
                <li><strong>Special Patch</strong>:
                    <const>PLAY</const>
                    the special patch and place it on your quilt board. The special patches are the only way to "patch" single spaces on your quilt board. If you receive the <strong>Special Patch</strong>, you will have to resolve it in the next turn. A special patch cannot be <const>SKIP</const>-ped. If you call <const>SKIP</const> while holding a special patch. Patch will be applied to the first free space on your quilt board.
                </li>
                <li><strong>Button Income</strong>: You receive a number of buttons according to the patches on your quilt board.
                </li>
            </ul>

            <img alt="Timeline explanation" src="https://cdn-games.codingame.com/community/3996809-1661712962734/9f0cb42961e9be42b40f61dfd2979bb13828be192ddcf1c67f9527e0bd43cecf.png"/>

            <!-- BEGIN level4 -->
                </div>
            <!-- END -->
            <!-- END -->
        </div>

            <!-- BEGIN level4 level5 -->
            
<h1>
            <br/>
            <span>Bonus Button</span>
        </h1>

            <!-- BEGIN level4 -->
                <div class="statement-new-league-rule">
                    <!-- END -->
        <div class="statement-rules-content">
            The first player to fully cover a 7x7 square of quilt board will receive <strong>Bonus Button</strong>. The <strong>Bonus Button</strong> can't be spent and it adds
            <const>7</const> points towards the final score.<br/><br/>

            <img src="https://cdn-games.codingame.com/community/3996809-1661712962734/c585bc886e1fc5b7a1d9a9a1d9f45f27335876b6b694124bc0ba8affcbe2ffd2.png"/>

            <!-- BEGIN level4 -->
                </div>
            <!-- END -->
        </div>
        <!-- END -->

        <h1>
                  <br/>
      <span class="icon icon-rules">&nbsp;</span>
            <span>End of the game and scoring</span>
        </h1>
        <div class="statement-rules-content">
            The game ends after both time tokens reach the last point of the <strong>Timeline</strong>. If a time token
            were to move past the last space, it simply stops on the last space.
            
            <!-- BEGIN level3 level4 level5 -->
            In case of a
            <const>SKIP</const>
            action, you only receive buttons for the actual number of spaces moved.
            <!-- END -->
            <br><br>

            Starting with
            <const>200</const>
            points
            <ul>
                <li>Subtract
                    <const>2</const>
                    points for each empty space on your quilt board
                </li>
                <!-- BEGIN level4 level5 -->
                <li>Add
                    <const>1</const>
                    point for each <strong>Button</strong> you have left
                </li>
                <!-- END -->
                <!-- BEGIN level5 -->
                <li>Add
                    <const>7</const> points if you own <strong>Bonus Button</strong></li>
                    <!-- END -->
            </ul>
                <!-- BEGIN level3 -->
                <div class="statement-new-league-rule">
                    Add <const>1</const>
                    point for each <strong>Button</strong> you have left
                </div>
                <br/>
                <!-- END -->
                    
                <!-- BEGIN level4 -->
                <div class="statement-new-league-rule">
                    Add
                    <const>7</const>
                    if you own <strong>Bonus Button</strong></li>
                </div>
                <br/>
                <!-- END -->
            The player with the higher score wins. In case of a tie, the player who gets to the final time point first wins.
        </div>

        <!-- Victory conditions -->
        <div class="statement-victory-conditions">
            <div class="icon victory"></div>
            <div class="blk">
                <div class="title">Victory Conditions</div>
                <div class="text">
                    <ul style="padding-top:0; padding-bottom: 0;">
                        <li>You have more points at the end of the game <strong>or</strong></li>
                        <li>You scored the same number of points, but you finished first</li>
                    </ul>
                </div>
            </div>
        </div>
        <!-- Lose conditions -->
        <div class="statement-lose-conditions">
            <div class="icon lose"></div>
            <div class="blk">
                <div class="title">Defeat Conditions</div>
                <div class="text">
                    <ul>
                        <li>Unknown command (only
                            <const>SKIP</const>
                            or
                            <const>PLAY</const>
                            are allowed)
                        </li>
                        <li>Your opponent has more points at the end of the game or he has the same number of points and finishes first
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <!-- EXPERT RULES -->
    <div class="statement-section statement-expertrules">
        <h1>
            <span class="icon icon-expertrules">&nbsp;</span>
            <span>Expert Rules</span>
        </h1>
        <div class="statement-expert-rules-content">

        <ul>
            <li>
                The UI supports single-line message bubbles. Anything after the last valid entry is considered a message.
            </li>
            <li>The patch with id <const>32</const> and <const>OO</const> shape is always last in the deck after initial shuffling as per official rules.</li>
            <li>
            Source code on <a href="https://github.com/VizGhar/CG-Patchwork">GitHub</a> (Kotlin).
            </li>
            <li>
            Design on <a href="https://www.figma.com/file/7diUIxEWdBphhC6MSSp48s/Patchwork---Codingame?node-id=0%3A1">Figma</a> made by me. Feel free to do whatever you want with the assets. 
            </li>
            <li>
            Official board game version on <a href="https://lookout-spiele.de/upload/en_patchwork.html_Rules_Patchwork_EN.pdf">Publisher's site</a>.
            </li>
            <li>
            There are some <a href="https://github.com/VizGhar/CG-Patchwork/blob/master/src/main/kotlin/com/codingame/game/Config.kt">Expert Mode fields</a> (if you don't know what Expert Mode is ask in forum/discord)
            </li>
            <li>
            Game is <a href="https://github.com/dreignier/cg-brutaltester">CG Brutaltester</a> compatible. Compiled referee can be found on <a href="https://github.com/VizGhar/CG-Patchwork/releases">releases page</a> of game repository. Instructions in <a href="https://github.com/VizGhar/CG-Patchwork">readme</a>.
            </li>
        </ul>
        </div>
    </div>
    <!-- PROTOCOL -->
    <div class="statement-section statement-protocol">
        <h1>
            <span class="icon icon-protocol">&nbsp;</span>
            <span>Game Input/Output</span>
        </h1>
        <!-- Protocol block -->
        <div class="blk">
            <div class="title">Initialization Input</div>
            <div class="text">
                <p><span class="statement-lineno">Line 1: </span>integer <var>incomeEvents</var> how many
                    <strong>Button Income</strong> events are there on the <strong>Timeline</strong>
                    <!-- BEGIN level1 level2 -->
                    <i>(always 0 for this league)</i>
                    <!-- END -->
                    </p>
                <p><span class="statement-lineno">Line 2: </span> <var>incomeEvents</var> integers of
                    <var>incomeTime</var> =
                    when the <strong>Button Income</strong> event will occur
                    </p>
                <p><span class="statement-lineno">Line 3: </span>integer <var>patchEvents</var> how many
                    <strong>Special Patch</strong> events are there on the <strong>Timeline</strong>
                    <!-- BEGIN level1 level2 level3 -->
                    <i>(always 0 for this league)</i>
                    <!-- END -->
                    </p>
                <p><span class="statement-lineno">Line 4: </span> <var>patchEvents</var> integers of
                    <var>patchTime</var> =
                    when the <strong>Special Patch</strong> event will occur</p>
            </div>
        </div>
        <!-- Protocol block -->
        <div class="blk">
            <div class="title">Game turn Input</div>
            <div class="text">
                <p><span class="statement-lineno">Line 1: </span> 3 integers <var>myButtons</var> = amount of
                    <strong>Buttons</strong> you currently own; <var>myTime</var> = how far is your token on the <strong>Timeline</strong>; <var>myEarning</var> = how much you will earn during <strong> Button Income</strong> event
                    <!-- BEGIN level1 level2 level3 -->
                    <i>(always 0 for this league)</i>
                    <!-- END -->
                    </p>
                    <p><span class="statement-lineno">Line 2-10: </span> 9 lines representing rows of your board -
                    <const>O</const>
                    is taken field
                    <const>.</const>
                    is empty</p>
                <p><span class="statement-lineno">Line 11: </span> 3 integers <var>opponentButtons</var> = amount of
                    <strong>Buttons</strong> your opponent currently owns; <var>opponentTime</var> = how far is your opponent's
                    token on the <strong>Timeline</strong>; <var>opponentEarning</var> = how much your opponent will earn during <strong> Button Income</strong> event
                    <!-- BEGIN level1 level2 level3 -->
                    <i>(always 0 for this league)</i>
                    <!-- END -->
                    </p>
                <p><span class="statement-lineno">Line 12-20: </span> 9 lines representing rows of opponent board -
                    <const>O</const>
                    is taken field
                    <const>.</const>
                    is empty
                </p>

                <p><span class="statement-lineno">Line 21: </span><var>patches</var> - count of patches not yet used
                <!-- BEGIN level1 level2 level3 -->
                 (you can play only the first 3 of these)
                 <!-- END -->
                <!-- BEGIN level4 level5 -->
                 (you can play only the first 3 of these if <var>specialPatchId</var> is <const>0</const>)
                 <!-- END -->
                </p>
                <p><span class="statement-lineno">Next <var>patches</var> lines:</span> <var>patchId</var>
                    <var>patchEarning</var> <var>patchButtonPrice</var> <var>patchTimePrice</var>
                    <var>patchShape</var></p>

                <p><span class="statement-lineno">Next line: </span>integer <var>specialPatchId</var>:
                <!-- BEGIN level1 level2 level3 -->
                ignore <i>(always 0 for this league)</i>
                <!-- END --> 
                <!-- BEGIN level4 level5 -->
                Id of special patch if available (<const>0</const> otherwise)
                <!-- END -->
                </p>
                                <p><span class="statement-lineno">Next line: </span> integer <var>opponentMoves</var>: how many moves your opponet played since your last move
                </p>

<p><span class="statement-lineno">Next <var>opponentMoves</var> lines:</span> <var>opponentMove</var> - move description (<const>SKIP</const> or <const>PLAY</const> with all its attributes except message)</p>
                To describe all fields of patch:
                <ul>
                    <li><var>patchId</var> - patch identifier in range <const>0 - 32</const>
                    </li>
                    <li><var>patchEarning</var> - how much will this patch earn during each <strong>Button Income</strong> event
                    <!-- BEGIN level1 level2 -->
                    <i>(ignore for this league)</i>
                    <!-- END -->
                    </li>
                    <li><var>patchButtonPrice</var> - how much buttons this patch costs</li>
                    <li><var>patchTimePrice</var> - how much time this patch costs</li>
                    <li><var>patchShape</var> - single string representing patch shape as follows:
                        <const>|</const>
                        separates rows of patch. Each rows consists of
                        <const>.</const>
                        and
                        <const>O</const>
                        characters.
                        <const>O</const>
                        means current square is taken by this patch.<br/><br/>
                        For example:
                        <var>patchShape</var> <const>O.O|OOO|O.O</const>
                        represents
                        <const>H</const>
                        shaped patch
                    </li>
                </ul>
            </div>
        </div>

        <!-- Protocol block -->
        <div class="blk">
            <div class="title">Output for one Game Turn</div>
            <div class="text">
                <span class="statement-lineno">A single line</span> with either of 2 actions:
                <ul>
                    <li>
                        <const>SKIP</const>
                    </li>
                    <li>
                        <const>PLAY</const>
                        <var>patchId</var> <var>x</var> <var>y</var>
                        <!-- BEGIN level2 level3 level4 level5 -->
                        <var>horizontalFlip</var> <var>rotations</var>
                        <!-- END -->
                    </li>
                </ul>

                <span class="statement-lineno">Example:</span><br/><br/>
<const>PLAY</const>
                <br><var>patchId</var><br>
                
                <!-- BEGIN level1 -->
                <var>x</var> and <var>y</var> - coordinates of top/left corner of your patch
                <br><br>
                <const>PLAY 3 1 2</const>
                - places patch with id
                <const>3</const>
                on position
                <const>1</const>
                <const>2</const>
                of you quilt board.
                <!-- END -->
                
                    <!-- BEGIN level2 level3 level4 level5 -->
                    <var>x</var> and <var>y</var> - coordinates of
                top/left corner of your patch after applying transformations (flip and/or rotations)
                    <br>
                    <var>horizontalFlip</var>
                -
                <const>1</const>
                if pach should be flipped horizontally first<br><var>rotations</var> - how many times to rotate patch clockwise
                <br><br>
                <const>PLAY 3 6 5 1 2</const>
                - places patch with id
                <const>3</const>
                on position
                <const>6</const>
                <const>5</const>
                of you quilt board. The patch will be flipped horizontally and rotated
                <const>2</const>
                time clockwise.
                <br>
                <br>
                    <!-- END -->

            </div>
        </div>
        <!-- Protocol block -->
        <div class="blk">
            <div class="title">Constraints</div>
            <div class="text">
                Duration of first turn -
                <const>1000ms</const>
                <br>
                Duration of game turn -
                <const>100ms</const>
                <br>
            </div>
        </div>
    </div>
</div>
