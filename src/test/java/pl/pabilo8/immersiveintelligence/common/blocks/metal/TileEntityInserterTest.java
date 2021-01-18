package pl.pabilo8.immersiveintelligence.common.blocks.metal;

import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.WireType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pabilo8.immersiveintelligence.common.wire.IIDataWireType;

class TileEntityInserterTest {
    public TileEntityInserter instace;
    @BeforeEach
    void setUp() {
        instace = new TileEntityInserter();
    }
    @AfterEach
    void tearDown() {
    }
    
    @Test
    void canTakeLV() {
        assert instace.canTakeLV();
    }
    
    @Test
    void canTakeMV() {
        assert instace.canTakeMV();
    }
    
    @Test
    void canConnect() {
        assert instace.canConnect();
    }
    
    @Test
    void isEnergyOutput() {
        assert instace.isEnergyOutput();
    }
    
    @Test
    void outputEnergy() {
        instace.outputEnergy(100, true, 1 );
    }
    
    @Test
    void canConnectCable() {
        TargetingInfo target = new TargetingInfo(EnumFacing.NORTH, 0.8f, 0.8f, 0.8f);
        assert instace.canConnectCable(new IIDataWireType(), target, new Vec3i(1,1,1));
        WireType wire = new WireType() {
            @Override
            public String getUniqueName() {
                return WireType.LV_CATEGORY;
            }
    
            @Override
            public String getCategory() {
                return WireType.LV_CATEGORY;
            }
    
            @Override
            public double getLossRatio() {
                return 0;
            }
    
            @Override
            public int getTransferRate() {
                return 0;
            }
    
            @Override
            public int getColour(ImmersiveNetHandler.Connection connection) {
                return 0;
            }
    
            @Override
            public double getSlack() {
                return 0;
            }
    
            @Override
            public TextureAtlasSprite getIcon(ImmersiveNetHandler.Connection connection) {
                return null;
            }
    
            @Override
            public int getMaxLength() {
                return 0;
            }
    
            @Override
            public ItemStack getWireCoil() {
                return null;
            }
    
            @Override
            public double getRenderDiameter() {
                return 0;
            }
    
            @Override
            public boolean isEnergyWire() {
                return true;
            }
        };
        assert !instace.canConnectCable(wire, target, new Vec3i(1, 1, 1));
        target = new TargetingInfo(EnumFacing.NORTH, 0.5f,0.5f,0.5f);
        assert instace.canConnectCable(wire,target,new Vec3i(1,1,1) );
        wire = new WireType() {
            @Override
            public String getUniqueName() {
                return WireType.MV_CATEGORY;
            }
    
            @Override
            public String getCategory() {
                return WireType.MV_CATEGORY;
            }
    
            @Override
            public double getLossRatio() {
                return 0;
            }
    
            @Override
            public int getTransferRate() {
                return 0;
            }
    
            @Override
            public int getColour(ImmersiveNetHandler.Connection connection) {
                return 0;
            }
    
            @Override
            public double getSlack() {
                return 0;
            }
    
            @Override
            public TextureAtlasSprite getIcon(ImmersiveNetHandler.Connection connection) {
                return null;
            }
    
            @Override
            public int getMaxLength() {
                return 0;
            }
    
            @Override
            public ItemStack getWireCoil() {
                return null;
            }
    
            @Override
            public double getRenderDiameter() {
                return 0;
            }
    
            @Override
            public boolean isEnergyWire() {
                return false;
            }
        };
        assert instace.canConnectCable(wire, target, new Vec3i(1,1,1));
        wire = new WireType() {
            @Override
            public String getUniqueName() {
                return WireType.HV_CATEGORY;
            }
    
            @Override
            public String getCategory() {
                return WireType.HV_CATEGORY;
            }
    
            @Override
            public double getLossRatio() {
                return 0;
            }
    
            @Override
            public int getTransferRate() {
                return 0;
            }
    
            @Override
            public int getColour(ImmersiveNetHandler.Connection connection) {
                return 0;
            }
    
            @Override
            public double getSlack() {
                return 0;
            }
    
            @Override
            public TextureAtlasSprite getIcon(ImmersiveNetHandler.Connection connection) {
                return null;
            }
    
            @Override
            public int getMaxLength() {
                return 0;
            }
    
            @Override
            public ItemStack getWireCoil() {
                return null;
            }
    
            @Override
            public double getRenderDiameter() {
                return 0;
            }
    
            @Override
            public boolean isEnergyWire() {
                return false;
            }
        };
        assert !instace.canConnectCable(wire,target, new Vec3i(1,1,1));
        
    }
    
    @Test
    void connectCable() {
    }
    
    @Test
    void getCableLimiter() {
        TargetingInfo target = new TargetingInfo(EnumFacing.NORTH, 0.5f,0.5f,0.5f);
        assert null == instace.getCableLimiter(target);
        target = new TargetingInfo(EnumFacing.NORTH, 0.8f,0.8f,0.8f);
        assert null == instace.getCableLimiter(target);
    }
    
    @Test
    void removeCable() {
    }
    
    @Test
    void getConnectionOffset() {
        assert new Vec3d(0.875f, 0.5f, 0.875f).equals(instace.getConnectionOffset(new ImmersiveNetHandler.Connection(new BlockPos(1, 1, 1), new BlockPos(1, 2, 1), WireType.getValue(WireType.LV_CATEGORY), 3)));
        assert new Vec3d(0.875f, 0.5f, 0.875f).equals(instace.getConnectionOffset(new ImmersiveNetHandler.Connection(new BlockPos(1, 1, 1), new BlockPos(1, 2, 1), WireType.getValue(WireType.MV_CATEGORY), 3)));
        assert new Vec3d(0.875f, 0.5f, 0.875f).equals(instace.getConnectionOffset(new ImmersiveNetHandler.Connection(new BlockPos(1, 1, 1), new BlockPos(1, 2, 1), WireType.getValue(WireType.HV_CATEGORY), 3)));
        assert new Vec3d(0.125f, 0.475f, 0.125f).equals(instace.getConnectionOffset(new ImmersiveNetHandler.Connection(new BlockPos(1, 1, 1), new BlockPos(1, 2, 1), new IIDataWireType(), 3)));
    }
    @Test
    void getTargetedConnector() {
    }
    
    @Test
    void getLimiter() {
    }
    
    @Test
    void receiveMessageFromServer() {
    }
    
    @Test
    void readCustomNBT() {
    }
    
    @Test
    void writeCustomNBT() {
    }
    
    @Test
    void hasCapability() {
    }
    
    @Test
    void getCapability() {
    }
    
    @Test
    void getBlockBounds() {
    }
    
    @Test
    void getComparatorInputOverride() {
    }
    
    @Test
    void readOnPlacement() {
    }
    
    @Test
    void getInventory() {
    }
    
    @Test
    void isStackValid() {
    }
    
    @Test
    void getSlotLimit() {
    }
    
    @Test
    void doGraphicalUpdates() {
    }
    
    @Test
    void update() {
    }
    
    @Test
    void hammerUseSide() {
    }
    
    @Test
    void getDataNetwork() {
    }
    
    @Test
    void setDataNetwork() {
    }
    
    @Test
    void onDataChange() {
    }
    
    @Test
    void getConnectorWorld() {
    }
    
    @Test
    void onPacketReceive() {
    }
    
    @Test
    void sendPacket() {
    }
    
    @Test
    void moveConnectionTo() {
    }
    
    @Test
    void getTileDrop() {
    }
}