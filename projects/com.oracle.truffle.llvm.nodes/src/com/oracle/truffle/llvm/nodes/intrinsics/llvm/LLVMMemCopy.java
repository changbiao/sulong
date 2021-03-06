/*
 * Copyright (c) 2016, Oracle and/or its affiliates.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.truffle.llvm.nodes.intrinsics.llvm;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.llvm.runtime.LLVMAddress;
import com.oracle.truffle.llvm.runtime.LLVMTruffleObject;
import com.oracle.truffle.llvm.runtime.LLVMVirtualAllocationAddress;
import com.oracle.truffle.llvm.runtime.global.LLVMGlobal;
import com.oracle.truffle.llvm.runtime.memory.LLVMMemMoveNode;
import com.oracle.truffle.llvm.runtime.memory.LLVMMemory;
import com.oracle.truffle.llvm.runtime.memory.UnsafeIntArrayAccess;
import com.oracle.truffle.llvm.runtime.nodes.api.LLVMExpressionNode;
import com.oracle.truffle.llvm.runtime.nodes.api.LLVMToNativeNode;

@NodeChildren({@NodeChild(type = LLVMExpressionNode.class, value = "destination"), @NodeChild(type = LLVMExpressionNode.class, value = "source"),
                @NodeChild(type = LLVMExpressionNode.class, value = "length"),
                @NodeChild(type = LLVMExpressionNode.class, value = "align"), @NodeChild(type = LLVMExpressionNode.class, value = "isVolatile")})
public abstract class LLVMMemCopy extends LLVMBuiltin {

    @Child private LLVMMemMoveNode memMove;

    public LLVMMemCopy(LLVMMemMoveNode memMove) {
        this.memMove = memMove;
    }

    @SuppressWarnings("unused")
    @Specialization
    protected Object doVoid(LLVMVirtualAllocationAddress target, LLVMVirtualAllocationAddress source, int length, int align, boolean isVolatile,
                    @Cached("getUnsafeIntArrayAccess()") UnsafeIntArrayAccess arrayAccess) {
        copy(arrayAccess, target, source, length);
        return null;
    }

    @SuppressWarnings("unused")
    @Specialization
    protected Object doVoid(LLVMVirtualAllocationAddress target, LLVMAddress source, int length, int align, boolean isVolatile,
                    @Cached("getLLVMMemory()") LLVMMemory memory,
                    @Cached("getUnsafeIntArrayAccess()") UnsafeIntArrayAccess arrayAccess) {
        copy(arrayAccess, memory, target, source.getVal(), length);
        return null;
    }

    @SuppressWarnings("unused")
    @Specialization
    protected Object doVoid(LLVMAddress target, LLVMVirtualAllocationAddress source, int length, int align, boolean isVolatile,
                    @Cached("getLLVMMemory()") LLVMMemory memory,
                    @Cached("getUnsafeIntArrayAccess()") UnsafeIntArrayAccess arrayAccess) {
        copy(arrayAccess, memory, target.getVal(), source, length);
        return null;
    }

    @SuppressWarnings("unused")
    @Specialization
    protected Object doVoid(VirtualFrame frame, LLVMGlobal target, LLVMVirtualAllocationAddress source, int length, int align, boolean isVolatile,
                    @Cached("createToNativeWithTarget()") LLVMToNativeNode globalAccess,
                    @Cached("getLLVMMemory()") LLVMMemory memory,
                    @Cached("getUnsafeIntArrayAccess()") UnsafeIntArrayAccess arrayAccess) {
        copy(arrayAccess, memory, globalAccess.executeWithTarget(frame, target).getVal(), source, length);
        return null;
    }

    @SuppressWarnings("unused")
    @Specialization
    protected Object doVoid(VirtualFrame frame, LLVMVirtualAllocationAddress target, LLVMGlobal source, int length, int align, boolean isVolatile,
                    @Cached("createToNativeWithTarget()") LLVMToNativeNode globalAccess,
                    @Cached("getLLVMMemory()") LLVMMemory memory,
                    @Cached("getUnsafeIntArrayAccess()") UnsafeIntArrayAccess arrayAccess) {
        copy(arrayAccess, memory, target, globalAccess.executeWithTarget(frame, source).getVal(), length);
        return null;
    }

    @SuppressWarnings("unused")
    @Specialization
    protected Object doVoid(LLVMVirtualAllocationAddress target, LLVMVirtualAllocationAddress source, long length, int align, boolean isVolatile,
                    @Cached("getUnsafeIntArrayAccess()") UnsafeIntArrayAccess arrayAccess) {
        copy(arrayAccess, target, source, length);
        return null;
    }

    @SuppressWarnings("unused")
    @Specialization
    protected Object doVoid(LLVMVirtualAllocationAddress target, LLVMAddress source, long length, int align, boolean isVolatile,
                    @Cached("getLLVMMemory()") LLVMMemory memory,
                    @Cached("getUnsafeIntArrayAccess()") UnsafeIntArrayAccess arrayAccess) {
        copy(arrayAccess, memory, target, source.getVal(), length);
        return null;
    }

    @SuppressWarnings("unused")
    @Specialization
    protected Object doVoid(LLVMAddress target, LLVMVirtualAllocationAddress source, long length, int align, boolean isVolatile,
                    @Cached("getLLVMMemory()") LLVMMemory memory,
                    @Cached("getUnsafeIntArrayAccess()") UnsafeIntArrayAccess arrayAccess) {
        copy(arrayAccess, memory, target.getVal(), source, length);
        return null;
    }

    @SuppressWarnings("unused")
    @Specialization
    protected Object doVoid(VirtualFrame frame, LLVMGlobal target, LLVMVirtualAllocationAddress source, long length, int align, boolean isVolatile,
                    @Cached("createToNativeWithTarget()") LLVMToNativeNode globalAccess,
                    @Cached("getLLVMMemory()") LLVMMemory memory,
                    @Cached("getUnsafeIntArrayAccess()") UnsafeIntArrayAccess arrayAccess) {
        copy(arrayAccess, memory, globalAccess.executeWithTarget(frame, target).getVal(), source, length);
        return null;
    }

    @SuppressWarnings("unused")
    @Specialization
    protected Object doVoid(VirtualFrame frame, LLVMVirtualAllocationAddress target, LLVMGlobal source, long length, int align, boolean isVolatile,
                    @Cached("createToNativeWithTarget()") LLVMToNativeNode globalAccess,
                    @Cached("getLLVMMemory()") LLVMMemory memory,
                    @Cached("getUnsafeIntArrayAccess()") UnsafeIntArrayAccess arrayAccess) {
        copy(arrayAccess, memory, target, globalAccess.executeWithTarget(frame, source).getVal(), length);
        return null;
    }

    @SuppressWarnings("unused")
    @Specialization
    protected Object doVoid(VirtualFrame frame, LLVMAddress target, LLVMAddress source, Object length, int align, boolean isVolatile) {
        memMove.executeWithTarget(frame, target, source, length);
        return null;
    }

    @SuppressWarnings("unused")
    @Specialization
    protected Object doVoid(VirtualFrame frame, LLVMGlobal target, LLVMAddress source, Object length, int align, boolean isVolatile,
                    @Cached("createToNativeWithTarget()") LLVMToNativeNode globalAccess) {
        memMove.executeWithTarget(frame, globalAccess.executeWithTarget(frame, target), source, length);
        return null;
    }

    @SuppressWarnings("unused")
    @Specialization
    protected Object doVoid(VirtualFrame frame, LLVMAddress target, LLVMGlobal source, Object length, int align, boolean isVolatile,
                    @Cached("createToNativeWithTarget()") LLVMToNativeNode globalAccess) {
        memMove.executeWithTarget(frame, target, globalAccess.executeWithTarget(frame, source), length);
        return null;
    }

    @SuppressWarnings("unused")
    @Specialization
    protected Object doVoid(VirtualFrame frame, LLVMGlobal target, LLVMGlobal source, Object length, int align, boolean isVolatile,
                    @Cached("createToNativeWithTarget()") LLVMToNativeNode globalAccess1,
                    @Cached("createToNativeWithTarget()") LLVMToNativeNode globalAccess2) {
        memMove.executeWithTarget(frame, globalAccess1.executeWithTarget(frame, target), globalAccess2.executeWithTarget(frame, source), length);
        return null;
    }

    @SuppressWarnings("unused")
    @Specialization
    protected Object doVoid(VirtualFrame frame, LLVMTruffleObject target, LLVMTruffleObject source, Object length, int align, boolean isVolatile) {
        memMove.executeWithTarget(frame, target, source, length);
        return null;
    }

    @SuppressWarnings("unused")
    @Specialization
    protected Object doVoid(VirtualFrame frame, LLVMTruffleObject target, LLVMAddress source, Object length, int align, boolean isVolatile) {
        memMove.executeWithTarget(frame, target, source, length);
        return null;
    }

    @SuppressWarnings("unused")
    @Specialization
    protected Object doVoid(VirtualFrame frame, LLVMAddress target, LLVMTruffleObject source, Object length, int align, boolean isVolatile) {
        memMove.executeWithTarget(frame, target, source, length);
        return null;
    }

    public static void copy(UnsafeIntArrayAccess arrayAccess, LLVMMemory memory, LLVMVirtualAllocationAddress target, long source, long length) {
        long sourcePointer = source;
        LLVMVirtualAllocationAddress targetAddress = target;
        for (long i = 0; i < length; i++) {
            byte value = memory.getI8(sourcePointer);
            targetAddress.writeI8(arrayAccess, value);
            targetAddress = targetAddress.increment(1);
            sourcePointer++;
        }
    }

    public static void copy(UnsafeIntArrayAccess arrayAccess, LLVMMemory memory, long target, LLVMVirtualAllocationAddress source, long length) {
        LLVMVirtualAllocationAddress sourcePointer = source;
        long targetAddress = target;
        for (long i = 0; i < length; i++) {
            byte value = sourcePointer.getI8(arrayAccess);
            sourcePointer = sourcePointer.increment(1);
            memory.putI8(targetAddress, value);
            targetAddress++;
        }
    }

    public static void copy(UnsafeIntArrayAccess memory, LLVMVirtualAllocationAddress target, LLVMVirtualAllocationAddress source, long length) {
        LLVMVirtualAllocationAddress sourcePointer = source;
        LLVMVirtualAllocationAddress targetAddress = target;
        for (long i = 0; i < length; i++) {
            byte value = sourcePointer.getI8(memory);
            sourcePointer = sourcePointer.increment(1);
            targetAddress.writeI8(memory, value);
            targetAddress = targetAddress.increment(1);
        }
    }
}
