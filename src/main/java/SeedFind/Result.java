package SeedFind;

/**
 * Class used to represent a seed "result", currently only successful results are created
 */
public class Result {
    public boolean success;
    public long Seed;
    public Position StrongholdPosition;
    public Position VillagePosition;
    public Position SpawnPosition;

    public Result(boolean success, long Seed, Position strongholdPosition, Position villagePosition, Position spawn){
        this.success = success;
        this.Seed = Seed;
        this.StrongholdPosition = strongholdPosition;
        this.VillagePosition = villagePosition;
        this.SpawnPosition = spawn;
    }
    @Override
    public String toString(){
        return "\n \n"+"Seed: " + this.Seed + " has these positions: \n"
                + "Stronghold: " + this.StrongholdPosition.Print() + "\n"
                + "Village: " + this.VillagePosition.Print() + "\n"
                + "Spawn: " + this.SpawnPosition.Print() + "\n"
                + "Teleport command to village: "
                + this.VillagePosition.PrintTp();

    }
}
