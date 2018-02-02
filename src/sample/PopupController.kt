package sample

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent


class PopupController{

    @FXML
    private lateinit var signedXdrBox:TextArea

    @FXML
    private lateinit var copyButton:Button

    fun initialize(){
        signedXdrBox.isDisable = true
        signedXdrBox.isWrapText = true
        copyButton.setOnAction {
            val content = ClipboardContent()
            content.putString(signedXdrBox.text)
            Clipboard.getSystemClipboard().setContent(content)
        }
    }
}