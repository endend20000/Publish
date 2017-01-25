package main;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.oro.text.regex.MalformedPatternException;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import expect4j.Closure;
import expect4j.Expect4j;
import expect4j.ExpectState;
import expect4j.matches.EofMatch;
import expect4j.matches.Match;
import expect4j.matches.RegExpMatch;
import expect4j.matches.TimeoutMatch;

public class Shell {

	// private static Logger log = Logger.getLogger(Shell.class);

	private Session session;
	private ChannelShell channel;
	private static Expect4j expect = null;
	private static final long defaultTimeOut = 1000;
	private StringBuffer buffer = new StringBuffer();

	public static final int COMMAND_EXECUTION_SUCCESS_OPCODE = -2;
	public static final String BACKSLASH_R = "\r";
	public static final String BACKSLASH_N = "\n";
	public static final String COLON_CHAR = ":";
	public static String ENTER_CHARACTER = BACKSLASH_R;
	public static final int SSH_PORT = 22;

	// ����ƥ�䣬���ڴ�����������صĽ��
	public static String[] linuxPromptRegEx = new String[] { "~]#", "~#", "#",
			":~#", "/$", ">" };

	public static String[] errorMsg = new String[] { "could not acquire the config lock " };

	// ssh��������ip��ַ
	private String ip;
	// ssh�������ĵ���˿�
	private int port;
	// ssh�������ĵ����û���
	private String user;
	// ssh�������ĵ�������
	private String password;

	public Shell() throws Exception {
		this.ip = ParameterReader.getPropertiesByKey("serviceIP");
		this.port = Integer.parseInt(ParameterReader
				.getPropertiesByKey("servicePort"));
		this.user = ParameterReader.getPropertiesByKey("serviceUser");
		this.password = ParameterReader.getPropertiesByKey("servicePassword");
		expect = getExpect();
	}

	/**
	 * �ر�SSHԶ������
	 */
	public void disconnect() {
		if (channel != null) {
			channel.disconnect();
		}
		if (session != null) {
			session.disconnect();
		}
	}

	/**
	 * ��ȡ���������ص���Ϣ
	 * 
	 * @return ����˵�ִ�н��
	 */
	public String getResponse() {
		return buffer.toString();
	}

	// ���Expect4j���󣬸ö��ÿ�����SSH������������
	private Expect4j getExpect() {
		try {
			Windows.printLog(String.format("��ʼ��½linux %s@%s:%s", user,ip, port));
			JSch jsch = new JSch();
			session = jsch.getSession(user, ip, port);
			session.setPassword(password);
			Hashtable<String, String> config = new Hashtable<String, String>();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			localUserInfo ui = new localUserInfo();
			session.setUserInfo(ui);
			session.connect();
			channel = (ChannelShell) session.openChannel("shell");
			Expect4j expect = new Expect4j(channel.getInputStream(),
					channel.getOutputStream());
			channel.connect();
			Windows.printLog(String.format("��½ %s@%s:%s �ɹ�!", user, ip, port));
			return expect;
		} catch (Exception ex) {
			Windows.printLog("��½ " + ip + ":" + port+ "ʧ�ܣ������û���������!");
			ex.printStackTrace();
			return null;
		}		
	}

	/**
	 * ִ����������
	 * 
	 * @param commands
	 *            Ҫִ�е����Ϊ�ַ�����
	 * @return ִ���Ƿ�ɹ�
	 */
	public boolean executeCommands(String[] commands) {
		// ���expect����Ϊ0��˵������û�гɹ�
		if (expect == null) {
			return false;
		}

		Windows.printLog("----------���������ִ��----------");
		for (String command : commands) {
			Windows.printLog(command);
		}
		Windows.printLog("--------------------");

		Closure closure = new Closure() {
			public void run(ExpectState expectState) throws Exception {
				//��ӡlinux���
				Windows.printLog(expectState.getBuffer());
				
				buffer.append(expectState.getBuffer());
				expectState.exp_continue();

			}
		};
		List<Match> lstPattern = new ArrayList<Match>();
		String[] regEx = linuxPromptRegEx;
		if (regEx != null && regEx.length > 0) {
			synchronized (regEx) {
				for (String regexElement : regEx) {
					try {
						RegExpMatch mat = new RegExpMatch(regexElement, closure);
						lstPattern.add(mat);
					} catch (MalformedPatternException e) {
						Windows.printLog("�����ʽ����");
						return false;
					} catch (Exception e) {
						Windows.printLog("����ִ���쳣");
						return false;
					}
				}
				lstPattern.add(new EofMatch(new Closure() {
							public void run(ExpectState state) {
							}
						}));
				lstPattern.add(new TimeoutMatch(defaultTimeOut, new Closure() {
					public void run(ExpectState state) {
					}
				}));
			}
		}
		try {
			boolean isSuccess = true;
			for (String strCmd : commands) {
				isSuccess = isSuccess(lstPattern, strCmd);
			}
			// ��ֹ���һ������ִ�в���
			isSuccess = !checkResult(expect.expect(lstPattern));

			// �Ҳ���������Ϣ��ʾ�ɹ�
			String response = buffer.toString().toLowerCase();
			//System.out.println(buffer);
			for (String msg : errorMsg) {
				if (response.indexOf(msg) > -1) {
					return false;
				}
			}

			return isSuccess;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	// ���ִ���Ƿ�ɹ�
	private boolean isSuccess(List<Match> objPattern, String strCommandPattern) {
		try {
			boolean isFailed = checkResult(expect.expect(objPattern));
			if (!isFailed) {
				expect.send(strCommandPattern);
				expect.send("\r");
				return true;
			}
			return false;
		} catch (MalformedPatternException ex) {
			return false;
		} catch (Exception ex) {
			return false;
		}
	}

	// ���ִ�з��ص�״̬
	private boolean checkResult(int intRetVal) {
		if (intRetVal == COMMAND_EXECUTION_SUCCESS_OPCODE) {
			return true;
		}
		return false;
	}

	// ����SSHʱ�Ŀ�����Ϣ
	// ���ò���ʾ�������롢����ʾ������Ϣ��
	public static class localUserInfo implements UserInfo {
		String passwd;

		public String getPassword() {
			return passwd;
		}

		public boolean promptYesNo(String str) {
			return true;
		}

		public String getPassphrase() {
			return null;
		}

		public boolean promptPassphrase(String message) {
			return true;
		}

		public boolean promptPassword(String message) {
			return true;
		}

		public void showMessage(String message) {

		}
	}

}