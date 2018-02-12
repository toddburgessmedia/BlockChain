package com.toddburgessmedia;

import com.google.gson.GsonBuilder;
import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;

public class NewBlock {

    static ArrayList<Block> blockchain = new ArrayList<Block>();

    static final int difficulty = 5;

    public static void main (String argv[]) {

          Disposable blockObserver = getBlockObservable()
              .subscribeWith(new DisposableObserver<Block>() {

                  @Override
                  public void onNext(Block block) {
                        blockchain.add(block);
                        blockchain.get(blockchain.size()-1).mineBlock(difficulty);

                  }

                  @Override
                  public void onError(Throwable e) {

                  }

                  @Override
                  public void onComplete() {

                  }
              });
          blockObserver.dispose();

        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println(blockchainJson);

        System.out.println("Blockchain is " + (isChainValid() ? "valid" : "invalid"));

    }

    static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;

        //loop through blockchain to check hashes:
        for(int i=1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            //compare registered hash and calculated hash:
            if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
                System.out.println("Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
        }
        return true;
    }

    static Observable<Block> getBlockObservable() {

        return Observable.create(new ObservableOnSubscribe<Block>() {
            @Override
            public void subscribe(ObservableEmitter<Block> emitter) throws Exception {
                int x;
                String hash;
                String message;
                for (x=0;x<9;x++) {
                    if (blockchain.size()==0) {
                        hash = "0";
                    } else {
                        hash = blockchain.get(blockchain.size()-1).hash;
                    }
                    message = "I am block " + x;
                    emitter.onNext(new Block(message,hash));
                }
                emitter.onComplete();
            }
        });

    }

}
