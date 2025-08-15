package nl.framegengine.editor.panels;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import imgui.type.ImString;
import nl.framegengine.core.entity.Material;
import nl.framegengine.core.entity.Texture;
import nl.framegengine.editor.EditorPanel;
import nl.framegengine.core.entity.GameObject;
import nl.framegengine.core.utils.ClassHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

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
        ImGui.setWindowFontScale(1f);
        ImGui.text(currentlySelectedObject.getClass().getSimpleName());
        ImGui.newLine();

        ImGui.pushStyleColor(ImGuiCol.FrameBg, 0.2f, 0.2f, 0.2f, 0.8f);
        ImGui.pushStyleColor(ImGuiCol.Header, 0.5f, 0.3f, 0.1f, 1f);
        ImGui.pushStyleColor(ImGuiCol.HeaderHovered, 0.625f, 0.3f, 0.05f, 1f);
        ImGui.pushStyleColor(ImGuiCol.HeaderActive, 0.75f, 0.3f, 0.05f, 1f);
        for (Field field : hierarchyObjects) {
            try {
                field.setAccessible(true);
                Object value = field.get(currentlySelectedObject);
                if(value == null) continue;
                drawOption(field, currentlySelectedObject);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        ImGui.popStyleColor(4);
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

    private void drawOption(Field field, Object drawingObject) throws IllegalAccessException {
        Object objectValue = field.get(drawingObject);

        ImGui.setWindowFontScale(1.1f);
        switch (objectValue) {
            case Float f -> {
                ImFloat ImFl = new ImFloat(f);
                if (ImGui.inputFloat(field.getName(), ImFl)) {
                    field.setAccessible(true);
                    field.set(drawingObject, ImFl.floatValue());
                }
            }
            case String str -> {
                ImString imStr = new ImString(str);
                if (ImGui.inputText(field.getName(), imStr)) {
                    field.setAccessible(true);
                    field.set(drawingObject, imStr.get());
                }
            }
            case Integer integer -> {
                ImInt imInteger = new ImInt(integer);
                if (ImGui.inputInt(field.getName(), imInteger)) {
                    field.setAccessible(true);
                    field.set(drawingObject, imInteger.get());
                }
            }
            case Boolean bool -> {
                ImBoolean imBool = new ImBoolean(bool);
                if (ImGui.checkbox(field.getName(), imBool)) {
                    field.setAccessible(true);
                    field.set(drawingObject, imBool.get());
                }
            }
            case Vector3f vector -> {
                float[] vec3Array = new float[]{vector.x, vector.y, vector.z};
                if (ImGui.inputFloat3(field.getName(), vec3Array)) {
                    vector.set(vec3Array[0], vec3Array[1], vec3Array[2]);
                    field.setAccessible(true);
                    field.set(drawingObject, vector);
                }
            }
            case Vector4f vector -> {
                float[] vec4Array = new float[]{vector.x, vector.y, vector.z, vector.w};
                if (ImGui.inputFloat4(field.getName(), vec4Array)) {
                    vector.set(vec4Array[0], vec4Array[1], vec4Array[2], vec4Array[3]);
                    field.setAccessible(true);
                    field.set(drawingObject, vector);
                }
            }
            case Quaternionf quaternion -> {
                float[] quaternionArray = new float[]{quaternion.x, quaternion.y, quaternion.z, quaternion.w};
                if (ImGui.inputFloat4(field.getName(), quaternionArray)) {
                    quaternion.set(quaternionArray[0], quaternionArray[1], quaternionArray[2], quaternionArray[3]);
                    field.setAccessible(true);
                    field.set(drawingObject, quaternion);
                }
            }
            case Texture texture -> {
                ImGui.text(field.getName());
                drawObject(texture);
            }
            case Material material -> {
                ImGui.text(field.getName());
                drawObject(material);
            }
            case Set<?> set -> {
                if(!set.isEmpty()) {
                    if(ImGui.collapsingHeader(field.getName())) {
                        AtomicInteger idx = new AtomicInteger();
                        idx.set(1);
                        set.forEach(setItem -> {
                            if(ImGui.collapsingHeader(setItem.getClass().getSimpleName() + " " + String.valueOf(idx.getAndIncrement()))) {
                                drawObject(setItem);
                            }
                        });
                    }
                }
            }
            case null, default -> {
                ImGui.text(field.getName());
                if(objectValue != null) ImGui.text(objectValue.toString());
            }
        }
        ImGui.setWindowFontScale(0.4f);
        ImGui.newLine();
        ImGui.setWindowFontScale(1f);
    }

    private void drawObject(Object object){
        //ImGui.text(object.getClass().getSimpleName());
        ImGui.indent(10);
        List<Field> objectFields = new ArrayList<>();
        ClassHelper.getAllPublicAndProtectedProperties(objectFields, object.getClass());
        objectFields.forEach(objectField -> {
            try {
                objectField.setAccessible(true);
                drawOption(objectField, object);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        ImGui.unindent(10);
    }
}