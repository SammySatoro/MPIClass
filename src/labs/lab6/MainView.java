package labs.lab6;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainView extends JFrame {

    public JButton addBlockButton;
    public JList<String> listView;
    public List<String> blockList;
    public int id = 1;
    public DefaultListModel<String> listModel;

    BlockChain blockChain = new BlockChain();
    Miner miner = new Miner();

    public MainView() {
        super("Blockchain");
        listModel = new DefaultListModel<>();
        listView = new JList<>(listModel);
        blockList = new ArrayList<>();
        Block block0 = new Block(0,"transaction1",Constants.GENESIS_PREV_HASH);
        miner.mine(block0, blockChain);
        blockList.add(0 + ") \t" +block0.getHash());
        listModel.addAll(blockList);
    }

    public void drawWindow() {
        setSize(500, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setFocusable(true);
        requestFocusInWindow();

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(400, 100));
        addBlockButton = new JButton("Add block");
        addBlockButton.addActionListener(e -> AddToList());



        topPanel.add(addBlockButton, BorderLayout.CENTER);
        getContentPane().add(listView);

        add(topPanel, BorderLayout.NORTH);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void startApp() {
        SwingUtilities.invokeLater(this::drawWindow);
    }

    private void UpdateUI() {
        listModel.clear();
        listModel.addAll(blockList.subList(Math.max(blockList.size() - 10, 0), blockList.size()));
    }

    private void AddToList() {
        Block block = new Block(id,"transaction" + (id + 1),blockChain.getBlockChain().get(blockChain.size()-1).getHash());
        miner.mine(block, blockChain);
        blockList.add(id + ") \t" + block.getHash());
        UpdateUI();
        id++;
    }
}