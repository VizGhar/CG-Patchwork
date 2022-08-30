package com.codingame.game

import com.codingame.gameengine.module.entities.Entity
import com.codingame.gameengine.module.entities.GraphicEntityModule
import com.codingame.gameengine.module.entities.Sprite
import com.codingame.gameengine.module.entities.Text
import com.google.inject.Inject
import com.google.inject.Singleton
import view.modules.InteractiveDisplayModule

private const val TILE_SIZE = 60

private val greens = arrayOf(
    0xbebaa4,
    0xbab6a0,
    0xb6b296,
    0xb2ad92,
    0xa8a98d,
)

private val oranges = arrayOf(
    0xc5b485,
    0xc9b889,
    0xcdbc8d,
    0xd1c091,
    0xd5c495,
)

data class TileEntity(val tileId: Int, val tile: Sprite, val priceTag: Entity<*>)

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

    fun hud(player1: Player, player2: Player) {
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
            if (it in EARNING_TURNS) {
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
                        it in EARNING_TURNS -> 0xFB7575
                        it in PATCH_TURNS -> 0x83DBD6
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

        player1MoneyText = g.createText("5")
            .setX(230)
            .setY(80)
            .setMaxWidth(100)
            .setZIndex(3)
            .setFontSize(32)
            .setFillColor(0xFFFFFF)

        player2MoneyText = g.createText("5")
            .setX(1690)
            .setY(80)
            .setMaxWidth(100)
            .setZIndex(3)
            .setFontSize(32)
            .setFillColor(0xFFFFFF)
            .setAnchorX(1.0)

        bonusButton = g.createSprite()
            .setAnchor(0.5)
            .setX(960)
            .setY(50)
            .setZIndex(3)
            .setImage("bonus.png")
    }

    private fun showTile(tile: Tile): TileEntity {
        val t = mutableListOf<Entity<*>>()

        val atile = g.createSprite()
            .setImage(tile.image)
            .setZIndex(100)
            .setScale(1.0)
            .setBaseWidth(TILE_SIZE * tile.shape.width)
            .setBaseHeight(TILE_SIZE * tile.shape.height)
            .also { t += it }

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
            .also { t += it }

        interactive.addResize(atile, atile, 1.0, 1000, InteractiveDisplayModule.HOVER_ONLY)

        return TileEntity(tile.id, atile, priceTag)
    }

    private val visibleTiles = mutableListOf<TileEntity>()

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
            existing.tile.isVisible = true
            existing.tile.x = 30 + index * availableTileWidth + availableTileWidth / 2
            existing.tile.y = offsetY
            existing.tile.setScale(0.2)
            existing.priceTag.isVisible = false
        }
    }

    fun showTilesBelt(tiles: List<Tile>, bonusTiles: List<Tile>) {
        tiles.take(3).forEachIndexed { i, tile ->
            var existing = visibleTiles.firstOrNull { it.tileId == tile.id }
            if (existing == null) { visibleTiles.add(showTile(tile)) }

            existing = visibleTiles.firstOrNull { it.tileId == tile.id }!!

            val offsetX = (g.world.width - 180) / 2
            val offsetY = 300 + i * 180 + (TILE_SIZE * (3 - tile.shape.height)) / 2 + TILE_SIZE*tile.shape.height / 2
            existing.priceTag.setVisible(true)
                .setY(offsetY + (tile.shape.height * TILE_SIZE - 60) / 2 - TILE_SIZE*tile.shape.height / 2)
                .setX(offsetX + tile.shape.width * TILE_SIZE / 2 + 20)
            existing.tile.setScale(0.8)
                .setVisible(true)
                .setY(offsetY)
                .setScale(1.0)
                .setX(offsetX)
                .setAnchor(0.5)
        }
        showPatchBelt(tiles.drop(3))

        bonusTiles.reversed().forEachIndexed { i, tile ->
            var existing2 = visibleTiles.firstOrNull { it.tileId == tile.id }
            if (existing2 == null) { visibleTiles.add(showTile(tile)) } else { return@forEachIndexed }
            existing2 = visibleTiles.firstOrNull { it.tileId == tile.id }!!
            existing2.priceTag.setVisible(false)
            existing2.tile
                .setAnchor(0.5)
                .setY(159)
                .setX(
                    when(i) {
                        0 -> 1431
                        1 -> 1311
                        2 -> 1071
                        3 -> 951
                        4 -> 831
                        else -> throw IllegalStateException()
                    }
                )
                .setScale(0.33)
                .setRotation(Math.random())
                .setZIndex(50 - i)
        }
        g.commitWorldState(0.1)
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
            tile.priceTag.isVisible = false
            g.commitWorldState(0.1)
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
            interactive.untrack(tile.tile)
            g.commitWorldState(0.98)
            tile.tile.setZIndex(10)
        } ?: throw IllegalStateException("No tile with tileid = $tileid found")
    }
}