package fr.framelab.model;

public class Project {
    private int id;
    private String name;
    private int challengeId;

    public Project(int id, String name, int challengeId) {
        this.id = id;
        this.name = name;
        this.challengeId = challengeId;
    }

    public Project(String name, int challengeId) {
        this(-1,name,challengeId);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(int challengeId) {
        this.challengeId = challengeId;
    }

    @Override
    public String toString () {
        return name;
    }
}
