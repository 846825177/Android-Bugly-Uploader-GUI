import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.Properties;

public class BuglyUploaderGui {
    private static final String CONFIG_FILE = "config.txt";
    private static JTextArea logArea = new JTextArea();

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        JFrame frame = new JFrame("🐞 Bugly Mapping Uploader");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        Font font = new Font("微软雅黑", Font.PLAIN, 14);
        UIManager.put("Label.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("Button.font", font);
        UIManager.put("TextArea.font", font);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.setContentPane(mainPanel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField appIdField = new JTextField();
        JTextField appKeyField = new JTextField();
        JTextField bundleIdField = new JTextField();
        JTextField versionField = new JTextField();
        JTextField versionCodeField = new JTextField();
        JTextField mappingPathField = new JTextField();
        JTextField jarPathField = new JTextField();

        JButton selectMappingButton = new JButton("📁 选择 mapping.txt");
        JButton selectJarButton = new JButton("📁 选择 jar 包");
        JButton uploadButton = new JButton("🚀 开始上传");

        int row = 0;
        addFormRow(formPanel, gbc, row++, "App ID:", appIdField);
        addFormRow(formPanel, gbc, row++, "App Key:", appKeyField);
        addFormRow(formPanel, gbc, row++, "Bundle ID:", bundleIdField);
        addFormRow(formPanel, gbc, row++, "Version:", versionField);
        addFormRow(formPanel, gbc, row++, "Version Code:", versionCodeField);
        addFormRowWithButton(formPanel, gbc, row++, "Mapping 路径:", mappingPathField, selectMappingButton);
        addFormRowWithButton(formPanel, gbc, row++, "JAR 包路径:", jarPathField, selectJarButton);

        mainPanel.add(formPanel, BorderLayout.NORTH);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        uploadButton.setPreferredSize(new Dimension(160, 40));
        actionPanel.add(uploadButton);
        mainPanel.add(actionPanel, BorderLayout.CENTER);

        logArea.setEditable(false);
        logArea.setBackground(new Color(245, 245, 245));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("日志输出"));
        scrollPane.setPreferredSize(new Dimension(780, 200));
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        // 加载配置
        loadConfig(appIdField, appKeyField, bundleIdField, versionField, versionCodeField, mappingPathField, jarPathField);

        selectMappingButton.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                mappingPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        selectJarButton.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                jarPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        uploadButton.addActionListener((ActionEvent e) -> {
            String appId = appIdField.getText();
            String appKey = appKeyField.getText();
            String bundleId = bundleIdField.getText();
            String version = versionField.getText();
            String versionCode = versionCodeField.getText();
            String mappingPath = mappingPathField.getText();
            String jarPath = jarPathField.getText();

            if (appId.isEmpty() || appKey.isEmpty() || bundleId.isEmpty() ||
                    version.isEmpty() || versionCode.isEmpty() ||
                    mappingPath.isEmpty() || jarPath.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "❗ 请填写所有字段", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            saveConfig(appId, appKey, bundleId, version, versionCode, mappingPath, jarPath);

            String command = String.format(
                    "java -jar \"%s\" -appid %s -appkey %s -bundleid %s -version %s -versionCode %s -platform Android -inputMapping \"%s\"",
                    jarPath, appId, appKey, bundleId, version, versionCode, mappingPath
            );

            logArea.setText("执行命令：\n" + command + "\n\n");

            new Thread(() -> runCommand(command)).start();
        });

        frame.setVisible(true);
    }

    private static void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private static void addFormRowWithButton(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field, JButton button) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        JPanel fieldWithButton = new JPanel(new BorderLayout(5, 0));
        fieldWithButton.add(field, BorderLayout.CENTER);
        fieldWithButton.add(button, BorderLayout.EAST);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(fieldWithButton, gbc);
    }

    private static void runCommand(String command) {
        try {
            ProcessBuilder builder = new ProcessBuilder("bash", "-c", command);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                appendLog(line);
            }

            int exitCode = process.waitFor();
            appendLog(exitCode == 0 ? "✅ 上传完成！" : "❌ 上传失败，退出码: " + exitCode);

        } catch (Exception ex) {
            appendLog("❌ 执行异常: " + ex.getMessage());
        }
    }

    private static void appendLog(String line) {
        SwingUtilities.invokeLater(() -> logArea.append(line + "\n"));
    }

    private static void saveConfig(String appId, String appKey, String bundleId,
                                   String version, String versionCode,
                                   String mappingPath, String jarPath) {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            Properties props = new Properties();
            props.setProperty("appid", appId);
            props.setProperty("appkey", appKey);
            props.setProperty("bundleid", bundleId);
            props.setProperty("version", version);
            props.setProperty("versionCode", versionCode);
            props.setProperty("mappingPath", mappingPath);
            props.setProperty("jarPath", jarPath);
            props.store(fos, "Bugly Upload Config");
        } catch (IOException e) {
            appendLog("❌ 保存配置失败：" + e.getMessage());
        }
    }

    private static void loadConfig(JTextField appId, JTextField appKey, JTextField bundleId,
                                   JTextField version, JTextField versionCode,
                                   JTextField mappingPath, JTextField jarPath) {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            Properties props = new Properties();
            props.load(fis);
            appId.setText(props.getProperty("appid", ""));
            appKey.setText(props.getProperty("appkey", ""));
            bundleId.setText(props.getProperty("bundleid", ""));
            version.setText(props.getProperty("version", ""));
            versionCode.setText(props.getProperty("versionCode", ""));
            mappingPath.setText(props.getProperty("mappingPath", ""));
            jarPath.setText(props.getProperty("jarPath", ""));
        } catch (IOException ignored) {
        }
    }
}
