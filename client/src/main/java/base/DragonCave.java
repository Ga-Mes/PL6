package base;

public class DragonCave {
    private Long depth;

    private Integer numberOfTreasures;

    public DragonCave(Long depth, Integer numberOfTreasures) {
        this.depth = depth;
        this.numberOfTreasures = numberOfTreasures;
    }

    public DragonCave() {}

    public Long getDepth() {
        return depth;
    }

    public void setDepth(Long depth) {
        this.depth = depth;
    }

    public Integer getNumberOfTreasures() {
        return numberOfTreasures;
    }

    public void setNumberOfTreasures(Integer numberOfTreasures) {
        this.numberOfTreasures = numberOfTreasures;
    }
}
