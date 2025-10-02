/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package symbolTable;

/**
 *
 * @author richter
 */

/**
 * Represents scope information used during tree traversal
 */
public class ScopeInfo {
    private ScopeType type;
    private String owner;  // Name of procedure/function, null for global/main
    
    public ScopeInfo(ScopeType type, String owner) {
        this.type = type;
        this.owner = owner;
    }
    
    public ScopeType getType() {
        return type;
    }
    
    public String getOwner() {
        return owner;
    }
    
    public void setType(ScopeType type) {
        this.type = type;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    @Override
    public String toString() {
        if (owner != null) {
            return type + " (owner: " + owner + ")";
        }
        return type.toString();
    }
}