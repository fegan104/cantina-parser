
import com.frankegan.Selector
import com.frankegan.getResourceAsText
import com.frankegan.getViewsForSelector
import com.frankegan.getViewsForSelectors
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Created by @author frankegan on 11/3/18.
 */
class AppTest {
    lateinit var jsonRoot: JsonElement

    @Before
    fun setUp(){
        val fileContent = getResourceAsText("/SystemViewController.json")
        val parser = JsonParser()
        jsonRoot = parser.parse(fileContent)
    }

    @Test
    fun testStackViews() {
        val stackViews = getViewsForSelector(jsonRoot, Selector.Class("StackView"))

        assertEquals(6, stackViews.size)
    }

    @Test
    fun testContainer() {
        val containers = getViewsForSelector(jsonRoot, Selector.ClassName("container"))

        assertEquals(6, containers.size)
    }

    @Test
    fun testWindowMode() {
        val windowModes = getViewsForSelector(jsonRoot, Selector.Identifier("windowMode"))

        assertEquals(1, windowModes.size)
    }

    @Test
    fun testStackColumn() {
        val result = getViewsForSelectors(jsonRoot, mutableListOf(
                Selector.Class("StackView"),
                Selector.ClassName("column")))

        assertEquals(3, result.size)
    }

    @Test
    fun testCVarVertical() {
        val result = getViewsForSelectors(jsonRoot, mutableListOf(
                Selector.Class("CvarSelect"),
                Selector.Identifier("verticalSync")))

        assertEquals(1, result.size)
    }
}