
import org.bitcoinj.core.*;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.MemoryBlockStore;
import org.bitcoinj.utils.BriefLogFormatter;

import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.Future;

public class getBlockTest {
    public static void main(String[] args) throws Exception {
    	String blockHashStr = "0000000000000a3290f20e75860d505ce0e948a1d1d846bec7e39015d242884b";//"000000000000000000212d479c8658cb9ccd98ca273afbf94cc836498b04aa4f";
    	
        BriefLogFormatter.init();
        System.out.println("Connecting to node");
        final NetworkParameters params = MainNetParams.get();
        
        BlockStore blockStore = new MemoryBlockStore(params);
        BlockChain chain = new BlockChain(params, blockStore);
        PeerGroup peerGroup = new PeerGroup(params, chain);
        peerGroup.start();
        PeerAddress addr = new PeerAddress(InetAddress.getLocalHost(), params.getPort());
        peerGroup.addAddress(addr);
        peerGroup.waitForPeers(1).get();
        Peer peer = peerGroup.getConnectedPeers().get(0);
        
        Sha256Hash blockHash = Sha256Hash.wrap(blockHashStr); //args[0]);
        Future<Block> future = peer.getBlock(blockHash);
        System.out.println("Waiting for node to send us the requested block: " + blockHash);
        Block block = future.get();
        System.out.println(block.getTime().toString());
        List<Transaction> trs = block.getTransactions();
        PrintTransactions(trs,params);
        //System.out.println(block);
        peerGroup.stopAsync();
    }
    static void PrintTransactions(List<Transaction> list, NetworkParameters params){
        System.out.println("block has nr of transactions: " + list.size());
        int counter = 1;
    	boolean first = true;
        for(Transaction t : list){
        	System.out.println("###### Transaction " + counter);
        	List<TransactionInput> ins = t.getInputs();
        	System.out.println("\tinputs: " + ins.size());
        	for (TransactionInput in: ins){
        		if (first){
        			first = false;
        			continue;
        		}
        		if(in.getValue() != null)
        			System.out.println("not null");
        		System.out.println("\t\t" + in.getFromAddress().toString());
//        		Script s = new Script(in.getScriptBytes());
//        		System.out.println();

        	}
        	List<TransactionOutput> outs = t.getOutputs();
        	System.out.println("\toutputs: " + outs.size());
        	for(TransactionOutput out : outs){
        		System.out.println("\t\t" + out.getAddressFromP2PKHScript(params) + " " + out.getValue().getValue() / 100000000.0);
        	}
        	System.out.println("\tSum: " + t.getOutputSum().getValue() / 100000000.0);
        	counter++;
        }
    }
}
