package nl.framegengine.editor;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import nl.framegengine.core.callbacks.NameEnteredCallback;

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
                if (!name.isEmpty() && nameEnteredCallback != null) {
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
}
