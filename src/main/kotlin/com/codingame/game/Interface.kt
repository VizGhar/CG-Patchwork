package com.codingame.game

import com.codingame.gameengine.module.entities.Entity
import com.codingame.gameengine.module.entities.GraphicEntityModule
import com.codingame.gameengine.module.entities.Sprite
import com.codingame.gameengine.module.entities.Text
import com.google.inject.Inject
import com.google.inject.Singleton
import view.modules.InteractiveDisplayModule

private const val AVATAR_SIZE = 136
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
    private var player1TimeText: Text? = null
    private var player2TimeText: Text? = null

    fun updateMoney(player1Money: Int, player2Money: Int) {
        player1MoneyText?.text = "$player1Money"
        player2MoneyText?.text = "$player2Money"
    }

    fun updateTime(player1Time: Int, player2Time: Int) {
        player1TimeText?.text = "Time passed: $player1Time"
        player2TimeText?.text = "Time passed: $player2Time"
    }

    fun hud(player1: Player, player2: Player) {
        g.createRectangle()
            .setX(0)
            .setY(0)
            .setWidth(g.world.width)
            .setHeight(g.world.height)
            .setFillColor(0x000000)
            .setZIndex(0)

        g.createTilingSprite()
            .setBaseWidth(g.world.width)
            .setBaseHeight(g.world.height)
            .setZIndex(0)
            .setAlpha(0.3)
            .setImage("background.jpeg")

        g.createRectangle()
            .setX(40)
            .setY(40)
            .setWidth(AVATAR_SIZE)
            .setHeight(AVATAR_SIZE)
            .setLineWidth(5.0)
            .setFillAlpha(0.0)
            .setZIndex(2)
            .setLineColor(player1.colorToken)

        g.createSprite()
            .setX(40)
            .setY(40)
            .setZIndex(0)
            .setImage(player1.avatarToken)
            .setBaseHeight(AVATAR_SIZE)
            .setBaseWidth(AVATAR_SIZE)

        g.createRectangle()
            .setX(g.world.width - 40 - AVATAR_SIZE)
            .setY(40)
            .setWidth(AVATAR_SIZE)
            .setHeight(AVATAR_SIZE)
            .setLineWidth(5.0)
            .setFillAlpha(0.0)
            .setZIndex(2)
            .setLineColor(player2.colorToken)

        g.createSprite()
            .setX(g.world.width - 40 - AVATAR_SIZE)
            .setY(40)
            .setZIndex(0)
            .setImage(player2.avatarToken)
            .setBaseHeight(AVATAR_SIZE)
            .setBaseWidth(AVATAR_SIZE)

        g.createText(player1.nicknameToken)
            .setX(40 + AVATAR_SIZE + 40)
            .setY(40)
            .setZIndex(3)
            .setFontSize(40)
            .setFillColor(player1.colorToken)

        g.createText(player2.nicknameToken)
            .setX(g.world.width - 40 - AVATAR_SIZE - 40)
            .setY(40)
            .setZIndex(3)
            .setFontSize(40)
            .setFillColor(player2.colorToken)
            .setAnchorX(1.0)

        g.createSprite()
            .setX(40 + AVATAR_SIZE + 40)
            .setY(103)
            .setZIndex(3)
            .setImage("button.png")

        g.createSprite()
            .setX(g.world.width - 40 - AVATAR_SIZE - 40)
            .setY(103)
            .setZIndex(3)
            .setImage("button.png")
            .setAnchorX(1.0)

        player1MoneyText = g.createText("5")
            .setX(40 + AVATAR_SIZE + 40 + 40 + 10)
            .setY(100)
            .setZIndex(3)
            .setFontSize(40)
            .setFillColor(0xFFFFFF)

        player2MoneyText = g.createText("5")
            .setX(g.world.width - 40 - AVATAR_SIZE - 40 - 40 - 10)
            .setY(100)
            .setZIndex(3)
            .setFontSize(40)
            .setFillColor(0xFFFFFF)
            .setAnchorX(1.0)

        player1TimeText = g.createText("Time passed: 0")
            .setX(40 + AVATAR_SIZE + 40)
            .setY(150)
            .setZIndex(3)
            .setFontSize(40)
            .setFillColor(0xFFFFFF)

        player2TimeText = g.createText("Time passed: 0")
            .setX(g.world.width - 40 - AVATAR_SIZE - 40)
            .setY(150)
            .setZIndex(3)
            .setFontSize(40)
            .setFillColor(0xFFFFFF)
            .setAnchorX(1.0)

        for (board in 0..1) {
            val offsetX = if (board == 0) 40 else g.world.width - 40 - 9 * TILE_SIZE
            val offsetY = 40 + AVATAR_SIZE + 40
            for (row in 0..8) {
                for (col in 0..8) {
                    val player = if (board == 0) player1 else player2
                    g.createRectangle()
                        .setX(offsetX + col * TILE_SIZE)
                        .setY(offsetY + row * TILE_SIZE)
                        .setZIndex(4)
                        .setWidth(TILE_SIZE)
                        .setHeight(TILE_SIZE)
                        .setFillColor(if (board == 0) greens.random() else oranges.random())
                        .setLineColor(player.colorToken)
                        .setLineWidth(1.0)
                        .setLineAlpha(0.3)
                        .setAlpha(0.2)
                }
            }

            g.createRectangle()
                .setX(offsetX - 10)
                .setY(offsetY - 10)
                .setZIndex(1)
                .setWidth(9 * TILE_SIZE + 20)
                .setHeight(9 * TILE_SIZE + 20)
                .setLineWidth(20.0)
                .setFillColor(if (board == 0) 0x91C7B1 else 0xE3D081)
                .setFillAlpha(1.0)
                .setLineColor(if (board == 0) player1.colorToken else player2.colorToken)
        }
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
                .setX(60)
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
            .setRotation(Math.PI / (5.0 + Math.random() * 7.0))
            .setZIndex(6)
            .also { t += it }

        interactive.addResize(atile, atile, 1.0, 1000, InteractiveDisplayModule.HOVER_ONLY)

        return TileEntity(tile.id, atile, priceTag)
    }

    private val visibleTiles = mutableListOf<TileEntity>()

    private fun showPatchBelt(tiles: List<Tile>) {
        if (tiles.isEmpty()) return
        val totalTilesCount = tiles.count()

        val availableTileWidth = g.world.width / totalTilesCount

        tiles.forEachIndexed { index, tile ->
            var existing = visibleTiles.firstOrNull { it.tileId == tile.id }
            if (existing == null) { visibleTiles.add(showTile(tile)) }

            existing = visibleTiles.firstOrNull { it.tileId == tile.id }!!

            val offsetY = 950
            existing.tile.setAnchor(0.5)
            existing.tile.isVisible = true
            existing.tile.x = index * availableTileWidth + availableTileWidth/2
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
            val offsetY = 200 + i * 220 + (TILE_SIZE * (3 - tile.shape.height)) / 2 + TILE_SIZE*tile.shape.height / 2
            existing.tile.isVisible = true
            existing.priceTag.isVisible = true
            existing.tile.y = offsetY
            existing.tile.setScale(1.0)
            existing.priceTag.y = offsetY + (tile.shape.height * TILE_SIZE - 60) / 2 - TILE_SIZE*tile.shape.height / 2
            existing.tile.x = offsetX
            existing.priceTag.x = offsetX + tile.shape.width * TILE_SIZE / 2 + 20
            existing.tile.setAnchor(0.5)
        }
        showPatchBelt(tiles.drop(3))

        bonusTiles.forEachIndexed { i, tile ->
            val count = bonusTiles.count()
            var existing2 = visibleTiles.firstOrNull { it.tileId == tile.id }
            if (existing2 == null) { visibleTiles.add(showTile(tile)) }
            existing2 = visibleTiles.firstOrNull { it.tileId == tile.id }!!
            existing2.priceTag.isVisible = false
            existing2.tile.y = 100
            existing2.tile.x = (g.world.width / 2 - 100 * (count-1).toDouble() / 2 + 100 * i).toInt()
            existing2.tile.setAnchor(0.5)
            existing2.tile.setScale(1.0)
        }
        g.commitWorldState(0.5)
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
        val offsetX = if (playerId == 0) 40 else g.world.width - 40 - 9 * TILE_SIZE
        val offsetY = 40 + AVATAR_SIZE + 40
        if (tileid == -1) return
        visibleTiles.firstOrNull { it.tileId == tileid }?.let { tile ->
            val anchor = anchors[mirrored to orientation] ?: throw IllegalStateException("Ooops this shouldn't happen. Orientation should be in range of 0-3. Please provide author of this game with this error message and shared replay.")
            tile.priceTag.isVisible = false
            tile.tile
                .setScaleX(if (mirrored) -1.0 else 1.0)
                .setAnchorX(anchor.first)
                .setAnchorY(anchor.second)
                .setRotation(Math.PI / 4 * orientation.toDouble() * 90)
                .setX(offsetX + x * TILE_SIZE)
                .setY(offsetY + y * TILE_SIZE)
                .setZIndex(900)
            visibleTiles.remove(tile)
            interactive.untrack(tile.tile)
            g.commitWorldState(1.0)
            tile.tile.setZIndex(10)
        } ?: throw IllegalStateException("No tile with tileid = $tileid found")
    }
}