package com.miao.genvalues;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class GenerateValueFiles {

	/**
	 * �Զ����������ļ�
	 * @param args ��ʽwidth height w,h_w,h_..._w,h;
	 */
	public static void main(String[] args) {
		// ��׼�ֱ���
		int baseW = 720, baseH = 1280;
		// Ҫ�������ɵķֱ���
		String addition = "";
		try {
			if (args.length >= 3) {
				// ����3������ʱ������׼width ��׼height ֧�ֵķֱ���
				baseW = Integer.parseInt(args[0]);
				baseH = Integer.parseInt(args[1]);
				addition = args[2];
				System.out.println("����[" + baseW + "*" + baseH + "]Ϊ��׼������[" + addition + "]��Ĭ��[" + SUPPORT_DIMESION
						+ "]�ֱ����У����������ļ�");
			} else if (args.length >= 2) {
				// ����2������ʱ�����Զ����׼�ֱ���
				baseW = Integer.parseInt(args[0]);
				baseH = Integer.parseInt(args[1]);
				System.out.println("����[" + baseW + "*" + baseH + "]Ϊ��׼������Ĭ�Ϸֱ���[" + SUPPORT_DIMESION + "]�������ļ�");
			} else if (args.length >= 1) {
				addition = args[0];
				System.out.println("��Ĭ�Ϸֱ���[" + baseW + "*" + baseH + "]Ϊ��׼������Ĭ�Ϸֱ���[" + addition + "]�������ļ�");
			}
		} catch (NumberFormatException e) {

			System.err.println("right input params : java -jar xxx.jar width height w,h_w,h_..._w,h;");
			e.printStackTrace();
			System.exit(-1);
		}

		new GenerateValueFiles(baseW, baseH, addition).generate();
	}

	/** ����xml�нڵ�ĸ�ʽ{0}-HEIGHT {1}-WIDTH*/
	private final static String WTemplate = "<dimen name=\"px{0}\">{1}px</dimen>\n";

	/** ���ɵ��ļ��и�ʽ{0}-HEIGHT {1}-WIDTH*/
	private final static String VALUE_TEMPLATE = "values-{0}x{1}";

	/** Ĭ����֧�ֵķֱ���*/
	private static final String SUPPORT_DIMESION = "320,480;" + "480,592;" + "480,728;" + "480,800;" + "480,854;"
			+ "540,888;" + "540,960;" + "552,1024;" + "600,800;" + "640,960;" + "600,1024;" + "672,1280;" + "720,1184;"
			+ "720,1196;" + "720,1280;" + "752,1280;" + "768,976;" + "768,1024;" + "768,1280;" + "800,1280;"
			+ "1080,1776;" + "1080,1800;" + "1080,1920;" + "1152,1920;" + "1440,2560;" + "1536,2048;" + "1536,2560;"
			+ "1600,2560;";

	private int baseW, baseH;
	private String dirStr = "./res";
	private String supportStr = SUPPORT_DIMESION;

	public GenerateValueFiles(int baseX, int baseY, String supportStr) {
		this.baseW = baseX;
		this.baseH = baseY;

		// ����ǰҪ���ɵķֱ��ʣ���ӵ�Ĭ�Ϸֱ�����
		if (!this.supportStr.contains(baseX + "," + baseY)) {
			this.supportStr += baseX + "," + baseY + ";";
		}

		// ��ȡ��ǰ����֧�ֵķֱ���
		this.supportStr += validateInput(supportStr);

		System.out.println(supportStr);

		// ��ǰĿ¼�´���res�ļ���
		File dir = new File(dirStr);
		if (!dir.exists()) {
			dir.mkdir();

		}
		System.out.println(dir.getAbsoluteFile());

	}

	/**
	 * ��������֧�ֵķֱ���
	 * @param supportStr w,h_...w,h;
	 * @return
	 */
	private String validateInput(String supportStr) {
		StringBuffer sb = new StringBuffer();
		String[] vals = supportStr.split("_");
		int w = -1;
		int h = -1;
		String[] wh;
		for (String val : vals) {
			try {
				if (val == null || val.trim().length() == 0)
					continue;

				wh = val.split(",");
				w = Integer.parseInt(wh[0]);
				h = Integer.parseInt(wh[1]);
			} catch (Exception e) {
				System.out.println("skip invalidate params : w,h = " + val);
				continue;
			}
			sb.append(w + "," + h + ";");
		}

		return sb.toString();
	}

	/**
	 * ���������ļ�
	 */
	public void generate() {
		String[] vals = supportStr.split(";");
		for (String val : vals) {
			String[] wh = val.split(",");
			generateXmlFile(Integer.parseInt(wh[0]), Integer.parseInt(wh[1]));
		}
	}

	/**
	 * ����xml�ļ�
	 * @param w
	 * @param h
	 */
	private void generateXmlFile(int w, int h) {

		// ׼��xmlͷ��root��ǩ��Ϣ
		StringBuffer sbForWidth = new StringBuffer();
		sbForWidth.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		sbForWidth.append("<resources>\n");

		// �����Ի�׼widthΪ��׼����ÿ1��׼����Ӧ�ĵ�ǰ���ش�С��
		// �� 480 / 720 = 0.66 ��ǰ��Ļ���Ϊ480���أ���720�ݣ�ÿһ��Ϊ0.66����
		float cellw = w * 1.0f / baseW;

		System.out.println("width : " + w + "," + baseW + "," + cellw);

		// �������е������Ӧֵ���Ի�׼�߶�Ϊ�������ɣ�
		for (int i = 1; i <= baseH; i++) {
			sbForWidth.append(WTemplate.replace("{0}", i + "").replace("{1}", change(cellw * i) + ""));
		}

		// xml�ļ���β
		sbForWidth.append("</resources>");

		// ����values-�߶�x��ȵ��ļ���
		File fileDir = new File(dirStr + File.separator + VALUE_TEMPLATE.replace("{0}", h + "").replace("{1}", w + ""));
		fileDir.mkdir();
		
		//����xml�ļ�
		File layxFile = new File(fileDir.getAbsolutePath(), "dimens.xml");
		try {
			//������д��xml�ļ�
			PrintWriter pw = new PrintWriter(new FileOutputStream(layxFile));
			pw.print(sbForWidth.toString());
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	//��ʽת��
	public static float change(float a) {
		int temp = (int) (a * 100);
		return temp / 100f;
	}

}