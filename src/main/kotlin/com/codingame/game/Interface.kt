package com.codingame.game

import com.codingame.gameengine.module.entities.*
import com.codingame.gameengine.module.toggle.ToggleModule
import com.codingame.gameengine.module.tooltip.TooltipModule
import com.google.inject.Inject
import com.google.inject.Singleton
import view.modules.InteractiveDisplayModule
import kotlin.random.Random

private const val TILE_SIZE = 60

data class TileEntity(
    val tileId: Int,
    val tile: Sprite,
    val priceTag: Entity<*>?,
    val trace: Rectangle?,
    val toggleTrace: Entity<*>?,
    val debugTrace: Entity<*>?
)

@Singleton
class Interface {

    @Inject
    private lateinit var toggleModule: ToggleModule

    @Inject
    private lateinit var tooltips: TooltipModule

    @Inject
    private lateinit var g: GraphicEntityModule

    @Inject
    private lateinit var interactive: InteractiveDisplayModule

    private val timeTokenOffset get() = if (league== League.L1) 771 else 430

    data class EarningButton(val playerId: Int, val x: Int, val y: Int, val scale: Double, val targetX: Int, val targetY: Int, val targetScale: Double, val sprite: Sprite)
    private var buttons: MutableList<EarningButton> = mutableListOf()
    private var player1MoneyText: Text? = null
    private var player2MoneyText: Text? = null
    private var player1IncomeMoneyText: Text? = null
    private var player2IncomeMoneyText: Text? = null
    private var player1TimeToken: Sprite? = null
    private var player2TimeToken: Sprite? = null
    private var bonusButton: Sprite? = null
    private var player1Message: Text? = null
    private var player2Message: Text? = null
    private var player1MessageGroup: Group? = null
    private var player2MessageGroup: Group? = null

    private val visibleTiles = mutableListOf<TileEntity>()

    fun updateMoney(from: Double, playerId: Int, player1Money: Int, player2Money: Int) {
        val btns = buttons.filter { it.playerId == playerId }
        g.commitEntityState(from, *btns.map { it.sprite }.toTypedArray())
        btns.forEach {
            it.sprite.setAlpha(1.0)
        }

        g.commitEntityState(from + 0.001, *btns.map { it.sprite }.toTypedArray())

        btns.forEach {
            it.sprite.setX(it.targetX, Curve.EASE_IN)
                .setY(it.targetY, Curve.EASE_IN)
                .setScale(it.targetScale, Curve.EASE_IN)
        }
        player1MoneyText?.text = "$player1Money"
        player2MoneyText?.text = "$player2Money"
    }

    fun updateIncome(from: Double, playerId: Int, income: Int) {
        val t = if (playerId == 0) {
            player1IncomeMoneyText!!
        } else {
            player2IncomeMoneyText!!
        }
        g.commitEntityState(from, t)

        t.setText("+ $income")
    }

    fun updateTime(from: Double, player1Time: Int, player2Time: Int, step: Boolean) {
        g.commitEntityState(from, player1TimeToken, player2TimeToken)
        val time1 = player1TimeToken ?: throw IllegalStateException()
        val time2 = player2TimeToken ?: throw IllegalStateException()
        if (!step) {
            time1.x = timeTokenOffset + 20 * player1Time
            time2.x = timeTokenOffset + 20 * player2Time
        } else {
            if (time1.x < timeTokenOffset + 20 * player1Time) {
                time1.x += 20
            } else if (time2.x < timeTokenOffset + 20 * player2Time) {
                time2.x += 20
            }
        }
    }

    fun acquireBonusBegin(from: Double) {
        g.commitEntityState(from, bonusButton)
        bonusButton
            ?.setX(g.world.width/2)
            ?.setY(g.world.height/2)
            ?.setScale(10.0)
            ?.setZIndex(5000)
            ?.setRotation(Math.PI)
    }

    fun acquireBonusMiddle(from: Double) {
        bonusButton
            ?.setRotation(2 * Math.PI)
    }

    fun acquireBonusEnd(from: Double, playerId: Int) {
        when(playerId) {
            0 -> bonusButton?.setX(325)?.setY(60)?.setScale(1.0)?.setZIndex(50)?.setRotation(Math.PI)
            1 -> bonusButton?.setX(1920-325)?.setY(60)?.setScale(1.0)?.setZIndex(50)?.setRotation(-2 * Math.PI)
            else -> {}
        }
    }

    fun initialize(
        player1: Player,
        player2: Player,
        player1Data: BoardManager.PlayerData,
        player2Data: BoardManager.PlayerData,
        patches: MutableList<Tile>,
        bonusPatches: MutableList<Tile>
    ) {
        g.createSprite()
            .setZIndex(0)
            .setX(0)
            .setY(0)
            .setImage("background.jpeg")

        if (league == League.L1) {
            g.createSprite()
                .setZIndex(1)
                .setX(747)
                .setY(142)
                .setImage("Half Timeline.png")
        } else {
            g.createSprite()
                .setZIndex(1)
                .setX(410)
                .setY(146)
                .setImage("Full Timeline.png")
        }

        g.createSprite()
            .setZIndex(100)
            .setX(30)
            .setY(0)
            .setImage("foreground.png")

        g.createRectangle()
            .setX(40)
            .setY(40)
            .setZIndex(0)
            .setFillColor(player1.colorToken)
            .setWidth(100)
            .setHeight(100)

        g.createRectangle()
            .setX(1780)
            .setY(40)
            .setZIndex(0)
            .setFillColor(player2.colorToken)
            .setWidth(100)
            .setHeight(100)

        player1TimeToken = g.createSprite()
            .setImage("TimeToken1.png")
            .setAnchorX(0.5)
            .setAnchorY(1.0)
            .setX(timeTokenOffset)
            .setY(140)
            .setZIndex(2)

        player2TimeToken = g.createSprite()
            .setImage("TimeToken2.png")
            .setAnchorX(0.5)
            .setAnchorY(0.0)
            .setX(timeTokenOffset)
            .setY(180)
            .setZIndex(2)

        for (it in league.earnTurns) {
            g.createSprite()
                .setBaseWidth(20)
                .setBaseHeight(20)
                .setAnchor(0.5)
                .setX(timeTokenOffset + it * 20)
                .setY(160)
                .setImage("button.png")
                .setZIndex(3)
        }

        g.createSprite()
            .setX(40)
            .setY(40)
            .setZIndex(1)
            .setImage(player1.avatarToken)
            .setBaseHeight(100)
            .setBaseWidth(100)

        g.createSprite()
            .setX(1780)
            .setY(40)
            .setZIndex(1)
            .setImage(player2.avatarToken)
            .setBaseHeight(100)
            .setBaseWidth(100)

        g.createText(player1.nicknameToken)
            .setX(40)
            .setY(160)
            .setZIndex(1)
            .setFontSize(32)
            .setFillColor(player1.colorToken)
            .setMaxWidth(280)

        g.createText(player2.nicknameToken)
            .setX(1880)
            .setY(160)
            .setZIndex(1)
            .setFontSize(32)
            .setFillColor(player2.colorToken)
            .setAnchorX(1.0)
            .setMaxWidth(280)

        player1MoneyText = g.createText(player1Data.money.toString())
            .setAnchor(0.5)
            .setX(250)
            .setY(100)
            .setMaxWidth(100)
            .setZIndex(3)
            .setFontSize(32)
            .setFillColor(0xFFFFFF)

        player2MoneyText = g.createText(player2Data.money.toString())
            .setAnchor(0.5)
            .setX(1670)
            .setY(100)
            .setMaxWidth(100)
            .setZIndex(3)
            .setFontSize(32)
            .setFillColor(0xFFFFFF)

        player1IncomeMoneyText = g.createText("+ ${player1Data.earning}")
            .setAnchor(0.5)
            .setX(350)
            .setY(100)
            .setMaxWidth(100)
            .setZIndex(3)
            .setFontSize(32)
            .setFillColor(0xFFFFFF)
            .also {
                toggleModule.displayOnToggleState(it, "tooltips", true)
            }

        player2IncomeMoneyText = g.createText("+ ${player2Data.earning}")
            .setAnchor(0.5)
            .setX(1570)
            .setY(100)
            .setMaxWidth(100)
            .setZIndex(3)
            .setFontSize(32)
            .setFillColor(0xFFFFFF)
            .also {
                toggleModule.displayOnToggleState(it, "tooltips", true)
            }

        if (league.scoreBonusMultiplier > 0) {
            bonusButton = g.createSprite()
                .setAnchor(0.5)
                .setX(960)
                .setY(50)
                .setZIndex(3)
                .setImage("bonus.png")
        }

        val box1 = g.createSprite()
            .setX(122)
            .setY(210)
            .setBaseWidth(500)
            .setBaseHeight(80)
            .setImage("messageBox1.png")

        player1Message = g.createText()
            .setMaxWidth(464)
            .setY(263)
            .setX(372)
            .setFillColor(0XFFFFFF)
            .setTextAlign(TextBasedEntity.TextAlign.CENTER)
            .setZIndex(4)
            .setFontWeight(Text.FontWeight.BOLDER)
            .setFontSize(35)
            .setAnchor(0.5)
        player1MessageGroup = g.createGroup(box1, player1Message)
            .setVisible(false)

        val box2 = g.createSprite()
            .setX(1302)
            .setY(210)
            .setBaseWidth(500)
            .setBaseHeight(80)
            .setImage("messageBox2.png")

        player2Message = g.createText()
            .setMaxWidth(464)
            .setY(263)
            .setX(1548)
            .setFillColor(0XFFFFFF)
            .setTextAlign(TextBasedEntity.TextAlign.CENTER)
            .setZIndex(4)
            .setFontWeight(Text.FontWeight.BOLDER)
            .setFontSize(30)
            .setAnchor(0.5)
        player2MessageGroup = g.createGroup(box2, player2Message)
            .setVisible(false)

        showAvailablePatches(patches.take(3))
        showPatchBelt(patches.drop(3))
        showBonusPatches(bonusPatches)

        val verticalOffset1 = 320
        val horizontalOffset1 = 100
        val verticalOffset2 = 320
        val horizontalOffset2 = 1280

        for(x in 0 until 9) {
            for (y in 0 until 9) {
                g.createRectangle()
                    .setY(y * 60 + verticalOffset2)
                    .setX(x * 60 + horizontalOffset2)
                    .setWidth(60)
                    .setHeight(60)
                    .setLineWidth(3.0)
                    .setZIndex(100000)
                    .setFillAlpha(0.1)
                    .also {
                        toggleModule.displayOnToggleState(it, "tooltips", true)
                        tooltips.setTooltipText(it, "Coordinates: $x,$y")
                    }

                g.createText("$x,$y")
                    .setAnchor(0.5)
                    .setY(y * 60 + verticalOffset2 + 30)
                    .setX(x * 60 + horizontalOffset2 + 30)
                    .setZIndex(200000)
                    .also {
                        toggleModule.displayOnToggleState(it, "tooltips", true)
                    }

                g.createRectangle()
                    .setY(y * 60 + verticalOffset1)
                    .setX(x * 60 + horizontalOffset1)
                    .setWidth(60)
                    .setHeight(60)
                    .setLineWidth(3.0)
                    .setZIndex(50)
                    .setZIndex(100000)
                    .setFillAlpha(0.1)
                    .also {
                        tooltips.setTooltipText(it, "Coordinates: $x,$y")
                        toggleModule.displayOnToggleState(it, "tooltips", true)
                    }

                g.createText("$x,$y")
                    .setAnchor(0.5)
                    .setY(y * 60 + verticalOffset1 + 30)
                    .setX(x * 60 + horizontalOffset1 + 30)
                    .setZIndex(200000)
                    .also {
                        toggleModule.displayOnToggleState(it, "tooltips", true)
                    }
            }
        }
    }

    private fun showTile(tile: Tile): TileEntity {
        val atile = g.createSprite()
            .setImage(tile.image)
            .setZIndex(100)
            .setScale(1.0)
            .setBaseWidth(TILE_SIZE * tile.shape.width)
            .setBaseHeight(TILE_SIZE * tile.shape.height)

        val priceTag = g.createGroup(
            g.createSprite()
                .setX(0)
                .setY(0)
                .setBaseWidth(200)
                .setBaseHeight(60)
                .setImage("pricetag.png"),
            g.createText("${tile.price} ")
                .setAnchorY(0.0)
                .setX(75)
                .setY(-7)
                .setZIndex(3)
                .setFontSize(40)
                .setFillColor(0x000000),
            g.createText("${tile.time} ")
                .setAnchorY(0.0)
                .setX(157)
                .setY(-7)
                .setZIndex(3)
                .setFontSize(40)
                .setFillColor(0x000000)
        )

        val trace = g.createRectangle()
            .setAlpha(0.0)
            .setWidth(TILE_SIZE * tile.shape.width)
            .setHeight(TILE_SIZE * tile.shape.height)
            .setZIndex(10000)

        val toggleTrace = g.createPolygon().apply {
            tile.polygon.forEach { (x, y) -> addPoint(x, y) }
        }.setAlpha(0.0)
            .setZIndex(900)

        val debugTrace = g.createPolygon().apply {
            tile.polygon.forEach { (x, y) -> addPoint(x, y) }
        }
            .setFillColor(Random.nextInt(0xFFFFFF))
            .setAlpha(1.0)
            .setZIndex(800)

        toggleModule.displayOnToggleState(atile, "tooltips", false)
        toggleModule.displayOnToggleState(toggleTrace, "tooltips", true)
        toggleModule.displayOnToggleState(trace, "tooltips", false)
        toggleModule.displayOnToggleState(debugTrace, "tooltips", true)

        interactive.addResize(trace, atile, 1.0, 1000, 0, InteractiveDisplayModule.HOVER_ONLY)
        interactive.addResize(trace, priceTag, 1.0, 1000, 100, InteractiveDisplayModule.HOVER_ONLY)

        tooltips.setTooltipText(toggleTrace, "Standard Tile\nTile id: ${tile.id}\nShape: ${tile.shape.string()}\nPrice-buttons: ${tile.price}\nPrice-time: ${tile.time}\nEarning: ${tile.earn}")

        return TileEntity(tile.id, atile, priceTag, trace, toggleTrace, debugTrace)
    }

    /**
     * Show remaining >= 30 patches on bottom of the screen
     */
    private fun showPatchBelt(tiles: List<Tile>) {
        if (tiles.isEmpty()) return
        val totalTilesCount = tiles.count()

        val availableTileWidth = (g.world.width - 80) / totalTilesCount

        tiles.forEachIndexed { index, tile ->
            var existing = visibleTiles.firstOrNull { it.tileId == tile.id }
            if (existing == null) { visibleTiles.add(showTile(tile)) }

            existing = visibleTiles.firstOrNull { it.tileId == tile.id }!!

            val offsetY = 979
            existing.tile.setAnchor(0.5)
                .setAlpha(1.0)
                .setX(30 + index * availableTileWidth + availableTileWidth / 2)
                .setY(offsetY)
                .setScale(0.2)

            existing.trace
                ?.setX(30 + index * availableTileWidth)
                ?.setY(910)
                ?.setWidth(availableTileWidth)
                ?.setHeight(150)

            existing.toggleTrace
                ?.setX((existing.tile.x - (existing.tile.baseWidth * existing.tile.scaleX)/2).toInt())
                ?.setY((existing.tile.y - (existing.tile.baseHeight * existing.tile.scaleY)/2).toInt())
                ?.setScale(0.2)

            existing.debugTrace
                ?.setX((existing.tile.x - (existing.tile.baseWidth * existing.tile.scaleX)/2).toInt())
                ?.setY((existing.tile.y - (existing.tile.baseHeight * existing.tile.scaleY)/2).toInt())
                ?.setScale(0.2)

            existing.priceTag
                ?.setX(30 + (index + 1) * availableTileWidth)
                ?.setY(offsetY)
                ?.setScale(0.0)
        }
    }

    /**
     * Show top 3 patches that can be purchased
     */
    private fun showAvailablePatches(tiles: List<Tile>) {
        tiles.forEachIndexed { i, tile ->
            var existing = visibleTiles.firstOrNull { it.tileId == tile.id }
            if (existing == null) { visibleTiles.add(showTile(tile)) }

            existing = visibleTiles.firstOrNull { it.tileId == tile.id }!!

            val offsetX = (g.world.width - 180) / 2
            val offsetY = 300 + i * 180 + (TILE_SIZE * (3 - tile.shape.height)) / 2 + TILE_SIZE*tile.shape.height / 2
            existing.priceTag
                ?.setY(offsetY + (tile.shape.height * TILE_SIZE - 60) / 2 - TILE_SIZE*tile.shape.height / 2)
                ?.setX(offsetX + tile.shape.width * TILE_SIZE / 2 + 20)
                ?.setScale(1.0)
            existing.tile.setScale(0.8)
                .setAlpha(1.0)
                .setY(offsetY)
                .setScale(1.0)
                .setX(offsetX)
                .setAnchor(0.5)
            existing.toggleTrace
                ?.setY(offsetY - existing.tile.baseHeight/2)
                ?.setScale(1.0)
                ?.setX(offsetX - existing.tile.baseWidth/2)
            existing.debugTrace
                ?.setY(offsetY - existing.tile.baseHeight/2)
                ?.setScale(1.0)
                ?.setX(offsetX - existing.tile.baseWidth/2)
        }
    }

    /**
     * Init - show bonus tiles on timeline
     */
    private fun showBonusPatches(bonusTiles: List<Tile>) {
        bonusTiles.forEach { tile ->
            val atile = g.createSprite()
                .setAnchor(0.5)
                .setBaseWidth(TILE_SIZE * tile.shape.width)
                .setBaseHeight(TILE_SIZE * tile.shape.height)
                .setScale(0.33)
                .setImage(tile.image)
                .setZIndex(100)
                .setY(159)
                .setX(429 - tile.id * 20)
                .setRotation(Math.random())

            val trace = g.createRectangle()
                .setAlpha(0.0)
                .setWidth(TILE_SIZE)
                .setHeight(TILE_SIZE)
                .setY(159 - TILE_SIZE / 2)
                .setX(429 - tile.id * 20 - TILE_SIZE / 2)
                .setZIndex(10000)

            val toggleTrace = g.createRectangle()
                .setAlpha(0.0)
                .setWidth(TILE_SIZE)
                .setHeight(TILE_SIZE)
                .setY(159 - TILE_SIZE / 2)
                .setX(429 - tile.id * 20 - TILE_SIZE / 2)
                .setZIndex(900)

            val debugTrace = g.createRectangle()
                .setAlpha(1.0)
                .setWidth(TILE_SIZE)
                .setHeight(TILE_SIZE)
                .setY(159 - TILE_SIZE / 2)
                .setX(429 - tile.id * 20 - TILE_SIZE / 2)
                .setZIndex(700)
                .setFillColor(Random.nextInt(0xFFFFFF))

            interactive.addResize(trace, atile, 1.0, 1000, 0, InteractiveDisplayModule.HOVER_ONLY)
            toggleModule.displayOnToggleState(atile, "tooltips", false)
            toggleModule.displayOnToggleState(toggleTrace, "tooltips", true)
            toggleModule.displayOnToggleState(debugTrace, "tooltips", true)
            tooltips.setTooltipText(toggleTrace, "BONUS PATCH\nTile id: ${tile.id}\nShape: ${tile.shape.string()}")
            visibleTiles.add(TileEntity(tile.id, atile, null, trace, toggleTrace, debugTrace))
        }
    }

    fun showTilesBelt(from: Double, tiles: List<Tile>) {
        g.commitEntityState(
            from,
            *(
                    visibleTiles.map { it.tile } +
                            visibleTiles.map { it.priceTag } +
                            visibleTiles.map { it.trace } +
                            visibleTiles.map { it.toggleTrace } +
                            visibleTiles.map { it.debugTrace }
                    ).filterNotNull().toTypedArray()
        )
        showAvailablePatches(tiles.take(3))
        showPatchBelt(tiles.drop(3))
    }

    private val anchors = mapOf(
        (false to 0) to (0.0 to 0.0),
        (false to 1) to (0.0 to 1.0),
        (false to 2) to (1.0 to 1.0),
        (false to 3) to (1.0 to 0.0),
        (true to 0) to (1.0 to 0.0),
        (true to 1) to (1.0 to 1.0),
        (true to 2) to (0.0 to 1.0),
        (true to 3) to (0.0 to 0.0)
    )

    fun move(from: Double, playerId: Int, tileid: Int, x: Int, y: Int, mirrored: Boolean, orientation: Int) {
        val offsetX = if (playerId == 0) 100 else g.world.width - 100 - 9 * TILE_SIZE
        val offsetY = 320
        if (tileid == -1) return
        visibleTiles.firstOrNull { it.tileId == tileid }?.let { tile ->
            val anchor = anchors[mirrored to orientation] ?: throw IllegalStateException("Ooops this shouldn't happen. Orientation should be in range of 0-3. Please provide author of this game with this error message and shared replay.")
            g.commitEntityState(from, *listOfNotNull(tile.priceTag, tile.tile, tile.toggleTrace, tile.debugTrace, player1IncomeMoneyText, player2IncomeMoneyText).toTypedArray())
            tile.priceTag?.setAlpha(0.0)
            tile.tile
                .setScaleX(if (mirrored) -1.0 else 1.0)
                .setScaleY(1.0)
                .setAnchorX(anchor.first)
                .setAnchorY(anchor.second)
                .setRotation(Math.PI / 4 * orientation.toDouble() * 90)
                .setX(offsetX + x * TILE_SIZE)
                .setY(offsetY + y * TILE_SIZE)
                .setZIndex(900)

            tile.toggleTrace
                ?.setScaleX(if (mirrored) -1.0 else 1.0)
                ?.setRotation(Math.PI / 4 * orientation.toDouble() * 90)
                ?.setScaleY(1.0)

            tile.debugTrace
                ?.setScaleX(if (mirrored) -1.0 else 1.0)
                ?.setRotation(Math.PI / 4 * orientation.toDouble() * 90)
                ?.setScaleY(1.0)

            when {
                !mirrored && orientation == 0 -> tile.toggleTrace?.setX(offsetX + x * TILE_SIZE)?.setY(offsetY + y * TILE_SIZE)
                !mirrored && orientation == 1 -> tile.toggleTrace?.setX(offsetX + x * TILE_SIZE + tile.tile.baseHeight)?.setY(offsetY + y * TILE_SIZE)
                !mirrored && orientation == 2 -> tile.toggleTrace?.setX(offsetX + x * TILE_SIZE + tile.tile.baseWidth)?.setY(offsetY + y * TILE_SIZE + tile.tile.baseHeight)
                !mirrored && orientation == 3 -> tile.toggleTrace?.setX(offsetX + x * TILE_SIZE)?.setY(offsetY + y * TILE_SIZE + tile.tile.baseWidth)
                mirrored && orientation == 0 -> tile.toggleTrace?.setX(offsetX + x * TILE_SIZE + tile.tile.baseWidth)?.setY(offsetY + y * TILE_SIZE)
                mirrored && orientation == 1 -> tile.toggleTrace?.setX(offsetX + x * TILE_SIZE + tile.tile.baseHeight)?.setY(offsetY + y * TILE_SIZE + tile.tile.baseWidth)
                mirrored && orientation == 2 -> tile.toggleTrace?.setX(offsetX + x * TILE_SIZE)?.setY(offsetY + y * TILE_SIZE + tile.tile.baseHeight)
                mirrored && orientation == 3 -> tile.toggleTrace?.setX(offsetX + x * TILE_SIZE)?.setY(offsetY + y * TILE_SIZE)
            }

            when {
                !mirrored && orientation == 0 -> tile.debugTrace?.setX(offsetX + x * TILE_SIZE)?.setY(offsetY + y * TILE_SIZE)
                !mirrored && orientation == 1 -> tile.debugTrace?.setX(offsetX + x * TILE_SIZE + tile.tile.baseHeight)?.setY(offsetY + y * TILE_SIZE)
                !mirrored && orientation == 2 -> tile.debugTrace?.setX(offsetX + x * TILE_SIZE + tile.tile.baseWidth)?.setY(offsetY + y * TILE_SIZE + tile.tile.baseHeight)
                !mirrored && orientation == 3 -> tile.debugTrace?.setX(offsetX + x * TILE_SIZE)?.setY(offsetY + y * TILE_SIZE + tile.tile.baseWidth)
                mirrored && orientation == 0 -> tile.debugTrace?.setX(offsetX + x * TILE_SIZE + tile.tile.baseWidth)?.setY(offsetY + y * TILE_SIZE)
                mirrored && orientation == 1 -> tile.debugTrace?.setX(offsetX + x * TILE_SIZE + tile.tile.baseHeight)?.setY(offsetY + y * TILE_SIZE + tile.tile.baseWidth)
                mirrored && orientation == 2 -> tile.debugTrace?.setX(offsetX + x * TILE_SIZE)?.setY(offsetY + y * TILE_SIZE + tile.tile.baseHeight)
                mirrored && orientation == 3 -> tile.debugTrace?.setX(offsetX + x * TILE_SIZE)?.setY(offsetY + y * TILE_SIZE)
            }

            val earning = tiles.firstOrNull { it.id == tileid }?.earn ?: 0
            if (earning > 0) {
                val buttonX = offsetX + x * TILE_SIZE + if (orientation % 2 == 0) tile.tile.baseWidth / 2 else tile.tile.baseHeight / 2
                val buttonY = offsetY + y * TILE_SIZE + if (orientation % 2 == 0) tile.tile.baseHeight / 2 else tile.tile.baseWidth / 2
                val buttonTargetX = if (playerId == 0) 200 else 1720
                val buttonTargetY = 100
                val buttonScale = 1.0 * earning
                val btn = g.createSprite()
                    .setImage("button.png")
                    .setRotation(tile.tile.rotation)
                    .setAnchor(0.5)
                    .setScale(buttonScale)
                    .setZIndex(20000)
                    .setX(buttonX)
                    .setY(buttonY)
                    .setAlpha(0.0)
                buttons.add(EarningButton(playerId, buttonX, buttonY, buttonScale, buttonTargetX, buttonTargetY, 1.0, btn))
            }

            interactive.untrack(tile.trace)
            tile.tile.setZIndex(10)
        } ?: throw IllegalStateException("No tile with tileid = $tileid found")
    }

    fun returnMoney() {
        buttons.forEach {
            it.sprite.setX(it.x).setY(it.y).setScale(it.scale).setAlpha(0.0)
        }
    }

    fun showMessage(playerId: Int, text: String) {
        when(playerId) {
            0 -> {
                player1Message?.text = text
                player1MessageGroup?.isVisible = text.isNotBlank()
            }
            1 -> {
                player2Message?.text = text
                player2MessageGroup?.isVisible = text.isNotBlank()
            }
        }
    }

    fun enlarge(patchId: Int) {
        visibleTiles.firstOrNull { it.tileId == patchId }?.let { tile ->
            tile.priceTag
                ?.setZIndex(500000)
                ?.setAlpha(1.0)
                ?.setY(tile.priceTag.y - 60)
                ?.setScale(3.0, Curve.EASE_IN_AND_OUT)
        }
    }

    fun pulseIn(from: Double, playerId: Int, change: Int) {
        val moneyText = when(playerId) {
            0 -> player1MoneyText
            else -> player2MoneyText
        }
        moneyText
            ?.setScale(5.0, Curve.LINEAR)
            ?.setText((moneyText.text.toInt() + change).toString())
    }

    fun pulseOut(from: Double, playerId: Int) {
        when(playerId) {
            0 -> player1MoneyText
            else -> player2MoneyText
        }?.also { g.commitEntityState(from, it) }
            ?.setScale(1.0, Curve.LINEAR)
    }

}