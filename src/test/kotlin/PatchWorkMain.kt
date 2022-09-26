import com.codingame.gameengine.runner.MultiplayerGameRunner

object PatchWorkMain {
    @JvmStatic
    fun main(args: Array<String>) {
        val gameRunner = MultiplayerGameRunner()
        gameRunner.addAgent(Boss4::class.java)
        gameRunner.addAgent(Boss4::class.java)
        gameRunner.setLeagueLevel(4)
        gameRunner.start()
    }
}