package nl.framegengine.editor.panels;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import imgui.type.ImString;
import nl.framegengine.editor.EditorPanel;
import nl.framegengine.core.entity.GameObject;
import nl.framegengine.core.utils.ClassHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class InfoPanel extends EditorPanel {

    private GameObject currentlySelectedObject = null;
    private List<Field> hierarchyObjects = new ArrayList<>();

    public InfoPanel(int posX, int posY, int sizeX, int sizeY) {
        super(posX, posY, sizeX, sizeY);
    }

    @Override
    public void renderFrame() {
        if(currentlySelectedObject == null) return;

        ImGui.setWindowFontScale(2f);
        ImGui.text(currentlySelectedObject.getName());
        ImGui.newLine();

        for (Field field : hierarchyObjects) {
            try {
                field.setAccessible(true);
                Object value = field.get(currentlySelectedObject);
                if(value == null) continue;
                drawOption(field);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 1f, 1f, 0f, 1f);
        ImGui.pushStyleColor(ImGuiCol.Text, .2f, .2f, .2f, 1f);
        if(currentlySelectedObject.isDrawDebugWireframe()){
            ImGui.pushStyleColor(ImGuiCol.Button, 0f, 1f, 0f, 1f);
            if(ImGui.button("Debugging frame")){
                currentlySelectedObject.setDrawDebugWireframe(false);
            }
        }else{
            ImGui.pushStyleColor(ImGuiCol.Button, 1f, 0f, 0f, 1f);
            if(ImGui.button("Debugging frame")){
                currentlySelectedObject.setDrawDebugWireframe(true);
            }
        }
        ImGui.popStyleColor(3);
    }

    public void setCurrentlySelectedObject(GameObject gameObject){
        currentlySelectedObject = gameObject;
        hierarchyObjects.clear();
        try {
            hierarchyObjects.add(ClassHelper.getFieldFromObject("localPosition", currentlySelectedObject.getClass()));
            hierarchyObjects.add(ClassHelper.getFieldFromObject("localRotation", currentlySelectedObject.getClass()));
            hierarchyObjects.add(ClassHelper.getFieldFromObject("scale", currentlySelectedObject.getClass()));
        } catch (NoSuchFieldException ignored) {}
        ClassHelper.getAllPublicAndProtectedProperties(hierarchyObjects, currentlySelectedObject.getClass());
    }

    private void drawOption(Field field) throws IllegalAccessException {
        Object objectValue = field.get(currentlySelectedObject);

        ImGui.setWindowFontScale(1.1f);
        switch (objectValue) {
            case Float f -> {
                ImFloat ImFl = new ImFloat(f);
                if (ImGui.inputFloat(field.getName(), ImFl)) {
                    field.setAccessible(true);
                    field.set(currentlySelectedObject, ImFl.floatValue());
                }
            }
            case String str -> {
                ImString imStr = new ImString(str);
                if (ImGui.inputText(field.getName(), imStr)) {
                    field.setAccessible(true);
                    field.set(currentlySelectedObject, imStr.get());
                }
            }
            case Integer integer -> {
                ImInt imInteger = new ImInt(integer);
                if (ImGui.inputInt(field.getName(), imInteger)) {
                    field.setAccessible(true);
                    field.set(currentlySelectedObject, imInteger.get());
                }
            }
            case Boolean bool -> {
                ImBoolean imBool = new ImBoolean(bool);
                if (ImGui.checkbox(field.getName(), imBool)) {
                    field.setAccessible(true);
                    field.set(currentlySelectedObject, imBool.get());
                }
            }
            case Vector3f vector -> {
                float[] vec3Array = new float[]{vector.x, vector.y, vector.z};
                if (ImGui.inputFloat3(field.getName(), vec3Array)) {
                    vector.set(vec3Array[0], vec3Array[1], vec3Array[2]);
                    field.setAccessible(true);
                    field.set(currentlySelectedObject, vector);
                }
            }
            case Vector4f vector -> {
                float[] vec4Array = new float[]{vector.x, vector.y, vector.z, vector.w};
                if (ImGui.inputFloat4(field.getName(), vec4Array)) {
                    vector.set(vec4Array[0], vec4Array[1], vec4Array[2], vec4Array[3]);
                    field.setAccessible(true);
                    field.set(currentlySelectedObject, vector);
                }
            }
            case Quaternionf quaternion -> {
                float[] quaternionArray = new float[]{quaternion.x, quaternion.y, quaternion.z, quaternion.w};
                if (ImGui.inputFloat4(field.getName(), quaternionArray)) {
                    quaternion.set(quaternionArray[0], quaternionArray[1], quaternionArray[2], quaternionArray[3]);
                    field.setAccessible(true);
                    field.set(currentlySelectedObject, quaternion);
                }
            }
            case null, default -> {
                ImGui.text(field.getName());
                assert objectValue != null;
                ImGui.text(objectValue.toString());
            }
        }
        ImGui.setWindowFontScale(0.4f);
        ImGui.newLine();
        ImGui.setWindowFontScale(1f);
    }
}