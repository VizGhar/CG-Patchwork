import com.codingame.gameengine.runner.MultiplayerGameRunner

object PatchWorkMain {
    @JvmStatic
    fun main(args: Array<String>) {
        val gameRunner = MultiplayerGameRunner()
        gameRunner.addAgent(Boss5::class.java)
        gameRunner.addAgent(Boss5::class.java)
        gameRunner.setLeagueLevel(5)
        gameRunner.start()
    }
}