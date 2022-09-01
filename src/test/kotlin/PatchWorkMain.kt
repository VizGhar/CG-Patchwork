import com.codingame.gameengine.runner.MultiplayerGameRunner

object PatchWorkMain {
    @JvmStatic
    fun main(args: Array<String>) {
        val gameRunner = MultiplayerGameRunner()
        gameRunner.addAgent(Agent2::class.java)
        gameRunner.addAgent(Agent2::class.java)
//        gameRunner.addAgent("python3 src/test/kotlin/agent.py");
        gameRunner.setLeagueLevel(4)
        gameRunner.start()
    }
}