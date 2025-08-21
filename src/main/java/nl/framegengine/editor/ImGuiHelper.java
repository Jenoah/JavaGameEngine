package nl.framegengine.editor;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import nl.framegengine.core.callbacks.NameEnteredCallback;
import org.joml.Math;

public class ImGuiHelper {
    private static boolean showNewFilePopup = false;
    private static final ImString newNameBuffer = new ImString(256);
    private static NameEnteredCallback nameEnteredCallback = null;

    public static void setInputFieldModal(NameEnteredCallback callback) {
        showNewFilePopup = true;
        newNameBuffer.set("");
        nameEnteredCallback = callback;
    }

    public static void showInputField(){
        if (showNewFilePopup) {
            ImGui.openPopup("Enter Name");
            showNewFilePopup = false;
        }

        if (ImGui.beginPopupModal("Enter Name", ImGuiWindowFlags.AlwaysAutoResize)) {
            ImGui.text("Enter the name:");
            ImGui.inputText("##name", newNameBuffer);

            if (ImGui.button("OK", 100, 0)) {
                String name = newNameBuffer.get().trim();
                if (!name.isBlank() && nameEnteredCallback != null) {
                    nameEnteredCallback.onNameEntered(name);
                }
                ImGui.closeCurrentPopup();
                nameEnteredCallback = null;
            }
            ImGui.sameLine();
            if (ImGui.button("Cancel", 100, 0)) {
                ImGui.closeCurrentPopup();
                nameEnteredCallback = null;
            }

            ImGui.endPopup();
        }
    }

    public static int calculateTextWidth(String[] items){
        float biggestWidth = 0;
        for (String item : items) {
            float textWidth = ImGui.calcTextSizeX(item);
            if(textWidth > biggestWidth) biggestWidth = textWidth;
        }

        return (int) Math.ceil(biggestWidth);
    }

    public static String guidFromName(String name){
        String guid = "";

        int i = name.lastIndexOf("##");
        if(i >= 0) i++;
        if (i > 0) {
            guid = name.substring(i+1);
        }
        return guid;
    }
}
