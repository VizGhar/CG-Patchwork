import com.codingame.gameengine.runner.MultiplayerGameRunner

object PatchWorkMain {
    @JvmStatic
    fun main(args: Array<String>) {
        val gameRunner = MultiplayerGameRunner()
        gameRunner.addAgent(League1Boss::class.java)
//        gameRunner.addAgent(League1Winner::class.java)
        gameRunner.addAgent("python3 src/test/kotlin/agent.py");
        gameRunner.setLeagueLevel(1)
        gameRunner.start()
    }
}