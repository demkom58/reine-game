package com.crown.graphic.gl.buffer;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL40.*;

public class GlDrawCmdSet extends GlMutableBuffer {
    private static final int COMMAND_STRUCT_BYTES = 4 * 4;
    private final int capacity;
    private final List<Command> commands;

    public GlDrawCmdSet(int hints, int capacity) {
        super(hints);

        this.capacity = capacity;
        this.commands = new ArrayList<>(capacity);

        bind(GL_COPY_WRITE_BUFFER);
        allocate(GL_COPY_WRITE_BUFFER, (long) capacity * COMMAND_STRUCT_BYTES);
        unbind(GL_COPY_WRITE_BUFFER);
    }

    /**
     * Binds buffer to indirect draw.
     */
    public void bind() {
        super.bind(GL_DRAW_INDIRECT_BUFFER);
    }

    /**
     * Unbinds the buffer from indirect draw.
     */
    public void unbind() {
        super.unbind(GL_DRAW_INDIRECT_BUFFER);
    }

    /**
     * Sets the draw commands to the buffer by index.
     *
     * @param idx the index of the command to set.
     * @param command the command to set.
     * @return previous command at the index or null if there was no command at the index.
     */
    public Command set(int idx, Command command) {
        if (idx >= capacity) {
            throw new IllegalArgumentException("Index " + idx + " out of range, capacity of the list is " + commands.size());
        }

        final Command previous = commands.get(idx);
        if (Objects.equals(previous, command)) {
            return null;
        }

        commands.set(idx, previous);

        ByteBuffer buf = null;
        try {
            buf = MemoryUtil.memAlloc(COMMAND_STRUCT_BYTES);
            command.write(buf);

            bind(GL_COPY_WRITE_BUFFER);
            upload(GL_COPY_WRITE_BUFFER, COMMAND_STRUCT_BYTES * idx, buf.flip());
            unbind(GL_COPY_WRITE_BUFFER);
        } finally {
            if (buf != null) {
                MemoryUtil.memFree(buf);
            }
        }

        return previous;
    }

    /**
     * Adds command to draw list and returns index of the command
     * or drawId that are same things in this case.
     *
     * @param command The command to add to the draw list.
     * @return existing id if command wasn't added, otherwise index of the new command.
     */
    public int add(Command command) {
        final int size = commands.size();
        if (size + 1 > capacity) {
            throw new IllegalArgumentException("List is full");
        }

        int index = commands.indexOf(command);
        if (index != -1) {
            return index;
        }

        commands.add(command);
        index = size;

        ByteBuffer buf = null;
        try {
            buf = MemoryUtil.memAlloc(COMMAND_STRUCT_BYTES);
            command.write(buf);

            bind(GL_COPY_WRITE_BUFFER);
            upload(GL_COPY_WRITE_BUFFER, COMMAND_STRUCT_BYTES * index, buf.flip());
            unbind(GL_COPY_WRITE_BUFFER);
        } finally {
            if (buf != null) {
                MemoryUtil.memFree(buf);
            }
        }

        return index;
    }

    /**
     * Removes command from draw list.
     *
     * @param command The command to remove from the draw list.
     * @return -1 if command wasn't found, otherwise index of the removed command.
     */
    public int remove(Command command) {
        int index = commands.indexOf(command);
        if (index != -1) {
            remove(index);
        }

        return index;
    }

    /**
     * Removes command from draw list by index.
     *
     * @param idx The index of the command to remove from the draw list.
     * @return previous command at the index or null if there was no command at the index.
     */
    public Command remove(int idx) {
        final int size = commands.size();
        if (idx >= size) {
            throw new IllegalArgumentException("Index " + idx + " out of range, size of the list is " + size);
        }

        final Command command = commands.remove(idx);
        commands.remove(command);

        bind(GL_COPY_READ_BUFFER);
        bind(GL_COPY_WRITE_BUFFER);

        final long nextIdx = idx + 1L;
        glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER,
                nextIdx * COMMAND_STRUCT_BYTES,
                (long) idx * COMMAND_STRUCT_BYTES,
                (size - nextIdx) * COMMAND_STRUCT_BYTES);

        unbind(GL_COPY_WRITE_BUFFER);
        unbind(GL_COPY_READ_BUFFER);

        return command;
    }

    /**
     * Returns current command count.
     */
    public int size() {
        return commands.size();
    }

    /**
     * Returns max size of the draw list.
     */
    public int capacity() {
        return capacity;
    }

    /**
     * Returns unmodifiable list of commands of this draw list.
     */
    public List<Command> commands() {
        return Collections.unmodifiableList(commands);
    }

    public record Command(int first, int count, int baseInstance, int instanceCount) {
        public void write(IntBuffer buf) {
            buf.put(count);
            buf.put(instanceCount);
            buf.put(first);
            buf.put(baseInstance);
        }

        public void write(ByteBuffer buf) {
            buf.putInt(count);
            buf.putInt(instanceCount);
            buf.putInt(first);
            buf.putInt(baseInstance);
        }
    }
}