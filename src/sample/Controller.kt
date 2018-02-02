package sample

import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Popup
import org.stellar.sdk.KeyPair
import org.stellar.sdk.Network
import org.stellar.sdk.Transaction
import org.stellar.sdk.xdr.TransactionEnvelope
import javafx.scene.Scene
import com.sun.javafx.robot.impl.FXRobotHelper.getChildren
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.layout.VBox
import javafx.stage.Stage


class Controller{

    @FXML
    private lateinit var xdrBox:TextArea

    @FXML
    private lateinit var signBox: TextField

    @FXML
    private lateinit var checkTest:CheckBox

    @FXML
    private lateinit var signButton:Button

    private var isXDRGood = false
    private var isSignatureGood = false

    fun initialize(){
        signButton.isDisable = true
        xdrBox.isWrapText = true
        xdrBox.textProperty().addListener { obs ->
                val xdrMessage = xdrBox.text.trim()
                isXDRGood = validateXDR(xdrMessage)
                signButton.isDisable = !(isXDRGood && isSignatureGood)
        }
        xdrBox.addEventFilter(KeyEvent.KEY_PRESSED){
            if(it.code == KeyCode.TAB){
                signBox.requestFocus()
                it.consume()
            }
        }

        signBox.textProperty().addListener { obs ->
            val sig = signBox.text
            isSignatureGood = validateKey(sig)
            signButton.isDisable = !(isXDRGood && isSignatureGood)
        }

        signButton.setOnAction { signTransaction() }

    }

    fun signTransaction(){
        if(checkTest.isSelected){ Network.useTestNetwork() } else { Network.usePublicNetwork() }

         val xdrMessage = xdrBox.text.replace("\n", "")
                 .replace("\t", "")
                 .replace("\r", "")
                 .replace(" ", "")
        println(xdrMessage)

        val xdrTransaction = TransactionEnvelope.decode(xdrMessage).tx
        val regTransaction = Transaction.fromXdr(xdrTransaction)
        regTransaction.sign(KeyPair.fromSecretSeed(signBox.text))
        openPopup(regTransaction.toEnvelopeXdrBase64())
    }

    fun openPopup(text: String){
        val root = FXMLLoader.load<Parent>(javaClass.getResource("signed-xdr.fxml"))
        val dialog = Stage()
        val dialogScene = Scene(root, 459.0, 280.0)

        val textArea = dialogScene.lookup("#signedXdrBox") as TextArea
        textArea.text = text
        dialog.scene = dialogScene
        dialog.show()
    }

    private fun validateKey(key:String):Boolean{
        return Regex("S[A-Z1-9]{55}").matches(key)
    }

    private fun validateXDR(text:String):Boolean{
        return Regex("[a-zA-Z0-9+/=]+").matches(text)
    }
}