package com.codingame.game

import com.codingame.gameengine.module.entities.*
import com.google.inject.Inject
import com.google.inject.Singleton
import view.modules.InteractiveDisplayModule

private const val TILE_SIZE = 60

data class TileEntity(val tileId: Int, val tile: Sprite, val priceTag: Entity<*>?, val trace: Rectangle?)

@Singleton
class Interface {

    @Inject
    private lateinit var g: GraphicEntityModule

    @Inject
    private lateinit var interactive: InteractiveDisplayModule

    private var player1MoneyText: Text? = null
    private var player2MoneyText: Text? = null
    private var player1TimeToken: Sprite? = null
    private var player2TimeToken: Sprite? = null
    private var bonusButton: Sprite? = null
    private var player1Message: Text? = null
    private var player2Message: Text? = null
    private var player1MessageGroup: Group? = null
    private var player2MessageGroup: Group? = null

    private val visibleTiles = mutableListOf<TileEntity>()

    fun updateMoney(player1Money: Int, player2Money: Int) {
        player1MoneyText?.text = "$player1Money"
        player2MoneyText?.text = "$player2Money"
    }

    fun updateTime(player1Time: Int, player2Time: Int) {
        player1TimeToken?.x = 430 + 20 * player1Time
        player2TimeToken?.x = 430 + 20 * player2Time
    }

    fun acquireBonus(playerId: Int) {
        when(playerId) {
            0 -> bonusButton?.setX(325)?.setY(60)
            1 -> bonusButton?.setX(1920-325)?.setY(60)
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
            .setX(430)
            .setY(140)
            .setZIndex(2)

        player2TimeToken = g.createSprite()
            .setImage("TimeToken2.png")
            .setAnchorX(0.5)
            .setAnchorY(0.0)
            .setX(430)
            .setY(180)
            .setZIndex(2)

        (0..53).forEach {
            if (it in league.earnTurns) {
                g.createSprite()
                    .setBaseWidth(20)
                    .setBaseHeight(20)
                    .setAnchor(0.5)
                    .setX(430 + it * 20)
                    .setY(160)
                    .setImage("button.png")
                    .setZIndex(3)
            }

            g.createCircle()
                .setRadius(6)
                .setX(430 + it * 20)
                .setY(160)
                .setZIndex(2)
                .setFillColor(
                    when {
                        it in league.earnTurns -> 0xFB7575
                        it in league.patchTurns -> 0x83DBD6
                        else -> 0x728AB7
                    }
                )
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
            .setX(230)
            .setY(80)
            .setMaxWidth(100)
            .setZIndex(3)
            .setFontSize(32)
            .setFillColor(0xFFFFFF)

        player2MoneyText = g.createText(player2Data.money.toString())
            .setX(1690)
            .setY(80)
            .setMaxWidth(100)
            .setZIndex(3)
            .setFontSize(32)
            .setFillColor(0xFFFFFF)
            .setAnchorX(1.0)

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
                .setFontFamily("Brush Script MT")
                .setX(75)
                .setY(7)
                .setZIndex(3)
                .setFontSize(40)
                .setFillColor(0x000000),
            g.createText("${tile.time} ")
                .setFontFamily("Brush Script MT")
                .setX(157)
                .setY(7)
                .setZIndex(3)
                .setFontSize(40)
                .setFillColor(0x000000)
        )

        val trace = g.createRectangle()
            .setAlpha(0.0)
            .setWidth(TILE_SIZE * tile.shape.width)
            .setHeight(TILE_SIZE * tile.shape.height)
            .setZIndex(10000)

        interactive.addResize(trace, atile, 1.0, 1000, 0, InteractiveDisplayModule.HOVER_ONLY)
        interactive.addResize(trace, priceTag, 1.0, 1000, 100, InteractiveDisplayModule.HOVER_ONLY)

        return TileEntity(tile.id, atile, priceTag, trace)
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

            interactive.addResize(trace, atile, 1.0, 1000, 0, InteractiveDisplayModule.HOVER_ONLY)

            visibleTiles.add(TileEntity(tile.id, atile, null, trace))
        }
    }

    fun showTilesBelt(tiles: List<Tile>) {
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

    fun move(playerId: Int, tileid: Int, x: Int, y: Int, mirrored: Boolean, orientation: Int) {
        val offsetX = if (playerId == 0) 100 else g.world.width - 100 - 9 * TILE_SIZE
        val offsetY = 320
        if (tileid == -1) return
        visibleTiles.firstOrNull { it.tileId == tileid }?.let { tile ->
            val anchor = anchors[mirrored to orientation] ?: throw IllegalStateException("Ooops this shouldn't happen. Orientation should be in range of 0-3. Please provide author of this game with this error message and shared replay.")
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
            visibleTiles.remove(tile)
            interactive.untrack(tile.trace)
            tile.tile.setZIndex(10)
        } ?: throw IllegalStateException("No tile with tileid = $tileid found")
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
}