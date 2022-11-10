package ar.edu.unlam.actas.model.merkletree;

import ar.edu.unlam.actas.utils.HashUtils;
import ar.edu.unlam.actas.model.Hashable;

import java.util.ArrayList;
import java.util.List;


public class MerkleTree<T extends Hashable> implements Hashable {
    public static final int TRANSACTION_LIMIT = 4;
    public static final int HASH_POSITION_A = 0;
    public static final int HASH_POSITION_B = 1;
    public static final int HASH_POSITION_C = 2;
    public static final int HASH_POSITION_D = 3;
    public static final int HASH_POSITION_AB = 4;
    public static final int HASH_POSITION_CD = 5;
    public static final int HASH_POSITION_ABCD = 6;
    //public static final int INITIAL_OFFSET = 3; not used

    private List<String> tree;
    private List<T> dataList;

    public MerkleTree() {
    }

    public MerkleTree(List<T> dataList) {
        this.dataList = dataList;
        this.tree = new ArrayList<>();

        //Cargamos las 4 transacciones necesarias
        for (T t : dataList)
            tree.add(t.getHash());

        //Calculamos los nodos intermedios
        tree.add(HashUtils.hash256(tree.get(HASH_POSITION_A) + tree.get(HASH_POSITION_B)));
        tree.add(HashUtils.hash256(tree.get(HASH_POSITION_C) + tree.get(HASH_POSITION_D)));

        //Calculamos el nodo root del MerkleTree
        tree.add(HashUtils.hash256(tree.get(HASH_POSITION_AB) + tree.get(HASH_POSITION_CD)));
    }

    @Override
    public String getHash() {
        return tree.get(HASH_POSITION_ABCD);
    }

    /**
     * Recalcula el root del MerkleTree
     * Si el hash del root no se modifica significa que el árbol
     * no fue modificado en ninguno de sus nodos
     *
     * @return el hash del root recalculado
     */
    @Override
    public String recalculateHash() {
        T b;
        String hashAcumWX = "";
        String hashAcumWXYZ = "";

        for (int i = 0; i < TRANSACTION_LIMIT; i++) {
            b = (T) dataList.get(i);
            String currHash = b.recalculateHash();
            hashAcumWX += currHash;

            if (i % 2 != 0) {
                hashAcumWX = HashUtils.hash256(hashAcumWX);
                hashAcumWXYZ += hashAcumWX;
                hashAcumWX = "";
            }
        }
        return HashUtils.hash256(hashAcumWXYZ);
    }

    public List<T> getDataList() {
        return dataList;
    }

    public List<String> getTree() {
        return tree;
    }

    public void setTree(List<String> tree) {
        this.tree = tree;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
}