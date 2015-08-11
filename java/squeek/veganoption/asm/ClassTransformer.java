package squeek.veganoption.asm;

import static org.objectweb.asm.Opcodes.*;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class ClassTransformer implements IClassTransformer
{

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		if (transformedName.equals("net.minecraft.block.BlockDynamicLiquid"))
		{
			boolean isObfuscated = !name.equals(transformedName);

			ClassNode classNode = readClassFromBytes(bytes);

			MethodNode method = findMethodNodeOfClass(classNode, isObfuscated ? "h" : "func_149813_h", isObfuscated ? "(Lahb;IIII)V" : "(Lnet/minecraft/world/World;IIII)V");

			/*
			if (Hooks.onFlowIntoBlock(null, 0, 0, 0, 0))
				return null;
			*/
			InsnList toInject = new InsnList();
			LabelNode ifNotCanceled = new LabelNode();
			toInject.add(new VarInsnNode(Opcodes.ALOAD, 1));
			toInject.add(new VarInsnNode(Opcodes.ILOAD, 2));
			toInject.add(new VarInsnNode(Opcodes.ILOAD, 3));
			toInject.add(new VarInsnNode(Opcodes.ILOAD, 4));
			toInject.add(new VarInsnNode(Opcodes.ILOAD, 5));
			toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(Hooks.class), "onFlowIntoBlock", "(Lnet/minecraft/world/World;IIII)Z", false));
			toInject.add(new JumpInsnNode(Opcodes.IFEQ, ifNotCanceled));
			toInject.add(new InsnNode(Opcodes.RETURN));
			toInject.add(ifNotCanceled);

			method.instructions.insertBefore(findFirstInstruction(method), toInject);

			return writeClassToBytes(classNode);
		}
		else if (transformedName.equals("net.minecraft.entity.item.EntityItem"))
		{
			boolean isObfuscated = !name.equals(transformedName);

			ClassNode classNode = readClassFromBytes(bytes);

			MethodNode method = findMethodNodeOfClass(classNode, isObfuscated ? "h" : "onUpdate", "()V");

			/*
			if (Hooks.onEntityItemUpdate(this))
				return;
			*/
			InsnList toInject = new InsnList();
			LabelNode ifNotCanceled = new LabelNode();
			toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
			toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(Hooks.class), "onEntityItemUpdate", "(Lnet/minecraft/entity/item/EntityItem;)Z", false));
			toInject.add(new JumpInsnNode(Opcodes.IFEQ, ifNotCanceled));
			toInject.add(new InsnNode(Opcodes.RETURN));
			toInject.add(ifNotCanceled);

			method.instructions.insertBefore(findFirstInstruction(method), toInject);

			return writeClassToBytes(classNode);
		}
		else if (transformedName.equals("net.minecraft.block.BlockPistonBase"))
		{
			boolean isObfuscated = !name.equals(transformedName);

			ClassNode classNode = readClassFromBytes(bytes);

			MethodNode method = findMethodNodeOfClass(classNode, isObfuscated ? "i" : "tryExtend", isObfuscated ? "(Lahb;IIII)Z" : "(Lnet/minecraft/world/World;IIII)Z");

			/*
			Hooks.onPistonTryExtend(world, x, y, z, facing)
			*/
			InsnList toInject = new InsnList();
			toInject.add(new VarInsnNode(Opcodes.ALOAD, 1));
			toInject.add(new VarInsnNode(Opcodes.ILOAD, 2));
			toInject.add(new VarInsnNode(Opcodes.ILOAD, 3));
			toInject.add(new VarInsnNode(Opcodes.ILOAD, 4));
			toInject.add(new VarInsnNode(Opcodes.ILOAD, 5));
			toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(Hooks.class), "onPistonTryExtend", "(Lnet/minecraft/world/World;IIII)V", false));

			method.instructions.insertBefore(findFirstInstruction(method), toInject);

			method = findMethodNodeOfClass(classNode, isObfuscated ? "a" : "onBlockEventReceived", isObfuscated ? "(Lahb;IIIII)Z" : "(Lnet/minecraft/world/World;IIIII)Z");

			/*
			Hooks.onPistonBlockEventReceived(world, x, y, z, event, data)
			*/
			toInject.clear();
			toInject.add(new VarInsnNode(Opcodes.ALOAD, 1));
			toInject.add(new VarInsnNode(Opcodes.ILOAD, 2));
			toInject.add(new VarInsnNode(Opcodes.ILOAD, 3));
			toInject.add(new VarInsnNode(Opcodes.ILOAD, 4));
			toInject.add(new VarInsnNode(Opcodes.ILOAD, 5));
			toInject.add(new VarInsnNode(Opcodes.ILOAD, 6));
			toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(Hooks.class), "onPistonBlockEventReceived", "(Lnet/minecraft/world/World;IIIII)V", false));

			method.instructions.insertBefore(getOrFindInstruction(method.instructions.getLast(), true).getPrevious(), toInject);

			return writeClassToBytes(classNode);
		}
		else if (transformedName.equals("net.minecraft.world.World"))
		{
			boolean isObfuscated = !name.equals(transformedName);

			ClassNode classNode = readClassFromBytes(bytes);

			// isFullCube
			MethodNode method = findMethodNodeOfClass(classNode, isObfuscated ? "q" : "func_147469_q", "(III)Z");

			LabelNode end = findEndLabel(method);
			AbstractInsnNode targetNode = findFirstInstruction(method);

			InsnList toInject = new InsnList();

			/*
			int isBlockFullCube = Hooks.isBlockFullCube(null, 0, 0, 0);
			if (isBlockFullCube != -1)
				return isBlockFullCube != 0;
			*/
			LabelNode varStartLabel = new LabelNode();
			LocalVariableNode localVar = new LocalVariableNode("isBlockFullCube", "I", method.signature, varStartLabel, end, method.maxLocals);
			method.maxLocals++;
			method.localVariables.add(localVar);

			toInject.add(new VarInsnNode(ALOAD, 0)); 					// this
			toInject.add(new VarInsnNode(ILOAD, 1)); 					// x
			toInject.add(new VarInsnNode(ILOAD, 2)); 					// y
			toInject.add(new VarInsnNode(ILOAD, 3)); 					// z
			toInject.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(Hooks.class), "isBlockFullCube", "(Lnet/minecraft/world/World;III)I", false));
			toInject.add(new VarInsnNode(ISTORE, localVar.index));		// isBlockFullCube = Hooks.isBlockFullCube(...)
			toInject.add(varStartLabel);								// variable scope start
			LabelNode label = new LabelNode();							// label if condition is true
			toInject.add(new VarInsnNode(ILOAD, localVar.index));		// isBlockFullCube
			toInject.add(new InsnNode(ICONST_M1));						// -1
			toInject.add(new JumpInsnNode(IF_ICMPEQ, label));			// isBlockFullCube != -1
			LabelNode labelReturnIf = new LabelNode();					// label if second condition is true
			toInject.add(new VarInsnNode(ILOAD, localVar.index));		// isBlockFullCube
			toInject.add(new JumpInsnNode(IFEQ, labelReturnIf));		// isBlockFullCube != 0
			toInject.add(new InsnNode(ICONST_1));						// 1 (true)
			toInject.add(new InsnNode(IRETURN));						// return true;
			toInject.add(labelReturnIf);								// if isBlockFullCube == 0, jump here
			toInject.add(new InsnNode(ICONST_0));						// 0 (false)
			toInject.add(new InsnNode(IRETURN));						// return false;
			toInject.add(label);										// if isBlockFullCube == -1, jump here

			method.instructions.insertBefore(targetNode, toInject);

			return writeClassToBytes(classNode);
		}
		else if (transformedName.equals("net.minecraftforge.fluids.BlockFluidFinite"))
		{
			// TODO: real check
			boolean isObfuscated = false;

			ClassNode classNode = readClassFromBytes(bytes);

			MethodNode method = findMethodNodeOfClass(classNode, isObfuscated ? "func_149674_a" : "updateTick", "(Lnet/minecraft/world/World;IIILjava/util/Random;)V");

			final String setBlockMethodName = isObfuscated ? "func_147446_b" : "setBlock";
			for (AbstractInsnNode insn = method.instructions.getFirst(); insn != null; insn = insn.getNext())
			{
				if (insn.getOpcode() == Opcodes.ICONST_2 && insn.getNext() != null && insn.getNext().getOpcode() == Opcodes.INVOKEVIRTUAL && ((MethodInsnNode) insn.getNext()).name.equals(setBlockMethodName))
				{
					AbstractInsnNode newInsn = new InsnNode(Opcodes.ICONST_3);
					method.instructions.insert(insn, newInsn);
					method.instructions.remove(insn);
					insn = newInsn;
				}
			}

			return writeClassToBytes(classNode, 0);
		}
		else if (transformedName.equals("tconstruct.tools.TinkerToolEvents"))
		{
			ClassNode classNode = readClassFromBytes(bytes);

			MethodNode method = findMethodNodeOfClass(classNode, "buildTool", "(Ltconstruct/library/event/ToolBuildEvent;)V");

			if (method != null)
			{
				/*
				event.handleStack = Hooks.getRealHandle(event.handleStack);
				*/
				InsnList toInject = new InsnList();
				toInject.add(new VarInsnNode(Opcodes.ALOAD, 1));
				toInject.add(new VarInsnNode(Opcodes.ALOAD, 1));
				toInject.add(new FieldInsnNode(Opcodes.GETFIELD, "tconstruct/library/event/ToolBuildEvent", "handleStack", "Lnet/minecraft/item/ItemStack;"));
				toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(Hooks.class), "getRealHandle", "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", false));
				toInject.add(new FieldInsnNode(Opcodes.PUTFIELD, "tconstruct/library/event/ToolBuildEvent", "handleStack", "Lnet/minecraft/item/ItemStack;"));

				method.instructions.insertBefore(findFirstInstruction(method), toInject);
				return writeClassToBytes(classNode);
			}
		}
		/*
		 * BEGIN Mystcraft descriptive book Burlap support
		 */
		else if (transformedName.equals("com.xcompwiz.mystcraft.data.RecipeLinkingbook"))
		{
			ClassNode classNode = readClassFromBytes(bytes);

			MethodNode method = findMethodNodeOfClass(classNode, "isValidCover", "(Lnet/minecraft/item/ItemStack;)Z");

			if (method != null)
			{
				/*
				if (Hooks.isLeather(itemStack))
					return true;
				*/
				InsnList toInject = new InsnList();
				LabelNode afterIf = new LabelNode();
				toInject.add(new VarInsnNode(Opcodes.ALOAD, 1));
				toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(Hooks.class), "isLeather", "(Lnet/minecraft/item/ItemStack;)Z", false));
				toInject.add(new JumpInsnNode(Opcodes.IFEQ, afterIf));
				toInject.add(new InsnNode(Opcodes.ICONST_1));
				toInject.add(new InsnNode(Opcodes.IRETURN));
				toInject.add(afterIf);

				method.instructions.insertBefore(findFirstInstruction(method), toInject);
				return writeClassToBytes(classNode);
			}
		}
		else if (transformedName.equals("com.xcompwiz.mystcraft.tileentity.TileEntityBookBinder"))
		{
			ClassNode classNode = readClassFromBytes(bytes);
			boolean didTransform = false;

			MethodNode method = findMethodNodeOfClass(classNode, "func_94041_b", "(ILnet/minecraft/item/ItemStack;)Z");

			if (method == null)
				method = findMethodNodeOfClass(classNode, "isItemValidForSlot", "(ILnet/minecraft/item/ItemStack;)Z");

			if (method != null)
			{
				/*
				if (slotIndex == 1 && Hooks.isLeather(itemStack))
					return true;
				*/

				InsnList toInject = new InsnList();
				LabelNode afterIf = new LabelNode();
				toInject.add(new VarInsnNode(Opcodes.ILOAD, 1));
				toInject.add(new InsnNode(Opcodes.ICONST_1));
				toInject.add(new JumpInsnNode(Opcodes.IF_ICMPNE, afterIf));
				toInject.add(new VarInsnNode(Opcodes.ALOAD, 2));
				toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(Hooks.class), "isLeather", "(Lnet/minecraft/item/ItemStack;)Z", false));
				toInject.add(new JumpInsnNode(Opcodes.IFEQ, afterIf));
				toInject.add(new InsnNode(Opcodes.ICONST_1));
				toInject.add(new InsnNode(Opcodes.IRETURN));
				toInject.add(afterIf);

				method.instructions.insertBefore(findFirstInstruction(method), toInject);
				didTransform = true;
			}

			method = findMethodNodeOfClass(classNode, "canBuildItem", "()Z");

			if (method != null)
			{
				AbstractInsnNode knownInsn = method.instructions.getFirst();
				while (knownInsn != null && (knownInsn.getOpcode() != Opcodes.GETSTATIC
						|| !((FieldInsnNode) knownInsn).owner.equals("net/minecraft/init/Items")
						|| !(((FieldInsnNode) knownInsn).name.equals("field_151116_aA") || ((FieldInsnNode) knownInsn).name.equals("leather"))))
				{
					knownInsn = knownInsn.getNext();
				}

				/*
				 * Convert from:
				 * 	    if (this.itemstacks[1].getItem() != Items.leather) return false;
				 * to:
				 * 		if (!Hooks.isLeather(this.itemstacks[1])) return false;
				 */
				if (knownInsn != null && knownInsn.getPrevious().getOpcode() == Opcodes.INVOKEVIRTUAL)
				{
					JumpInsnNode jumpInsn = (JumpInsnNode) knownInsn.getNext();
					jumpInsn.setOpcode(Opcodes.IFNE);

					InsnList replacementInsns = new InsnList();
					replacementInsns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(Hooks.class), "isLeather", "(Lnet/minecraft/item/ItemStack;)Z", false));
					method.instructions.insert(knownInsn, replacementInsns);

					method.instructions.remove(knownInsn.getPrevious()); // itemStack.getItem() INVOKEVIRTUAL
					method.instructions.remove(knownInsn); // Items.leather GETSTATIC

					didTransform = true;
				}
			}

			if (didTransform)
				return writeClassToBytes(classNode);
		}
		/*
		 * END Mystcraft descriptive book Burlap support
		 */
		return bytes;
	}

	private ClassNode readClassFromBytes(byte[] bytes)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		return classNode;
	}

	private byte[] writeClassToBytes(ClassNode classNode)
	{
		return writeClassToBytes(classNode, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
	}

	private byte[] writeClassToBytes(ClassNode classNode, int flags)
	{
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}

	private MethodNode findMethodNodeOfClass(ClassNode classNode, String methodName, String methodDesc)
	{
		for (MethodNode method : classNode.methods)
		{
			if (method.name.equals(methodName) && method.desc.equals(methodDesc))
			{
				return method;
			}
		}
		return null;
	}

	public AbstractInsnNode getOrFindInstruction(AbstractInsnNode firstInsnToCheck)
	{
		return getOrFindInstruction(firstInsnToCheck, false);
	}

	public AbstractInsnNode getOrFindInstruction(AbstractInsnNode firstInsnToCheck, boolean reverseDirection)
	{
		for (AbstractInsnNode instruction = firstInsnToCheck; instruction != null; instruction = reverseDirection ? instruction.getPrevious() : instruction.getNext())
		{
			if (instruction.getType() != AbstractInsnNode.LABEL && instruction.getType() != AbstractInsnNode.LINE)
				return instruction;
		}
		return null;
	}

	public AbstractInsnNode findFirstInstruction(MethodNode method)
	{
		return getOrFindInstruction(method.instructions.getFirst());
	}

	public LabelNode findEndLabel(MethodNode method)
	{
		for (AbstractInsnNode instruction = method.instructions.getLast(); instruction != null; instruction = instruction.getPrevious())
		{
			if (instruction instanceof LabelNode)
				return (LabelNode) instruction;
		}
		return null;
	}
}
