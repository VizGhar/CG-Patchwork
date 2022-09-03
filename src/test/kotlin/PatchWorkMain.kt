import com.codingame.gameengine.runner.MultiplayerGameRunner

object PatchWorkMain {
    @JvmStatic
    fun main(args: Array<String>) {
        val gameRunner = MultiplayerGameRunner()
//        gameRunner.addAgent(League1Boss::class.java)
//        gameRunner.addAgent(League1Winner::class.java)
//        gameRunner.addAgent("python3 src/test/kotlin/agent.py");

        gameRunner.addAgent(Boss2::class.java)
        gameRunner.addAgent(Boss2::class.java)
        gameRunner.setLeagueLevel(2)
        gameRunner.start()
    }
}