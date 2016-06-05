package com.miao.genvalues;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class GenerateValueFiles {

	/**
	 * 自动生成适配文件
	 * @param args 格式width height w,h_w,h_..._w,h;
	 */
	public static void main(String[] args) {
		// 基准分别率
		int baseW = 720, baseH = 1280;
		// 要单独生成的分辨率
		String addition = "";
		try {
			if (args.length >= 3) {
				// 输入3个参数时，即基准width 基准height 支持的分辨率
				baseW = Integer.parseInt(args[0]);
				baseH = Integer.parseInt(args[1]);
				addition = args[2];
				System.out.println("设置[" + baseW + "*" + baseH + "]为基准，增加[" + addition + "]到默认[" + SUPPORT_DIMESION
						+ "]分辨率中，生成适配文件");
			} else if (args.length >= 2) {
				// 输入2个参数时，即自定义基准分辨率
				baseW = Integer.parseInt(args[0]);
				baseH = Integer.parseInt(args[1]);
				System.out.println("设置[" + baseW + "*" + baseH + "]为基准，生成默认分辨率[" + SUPPORT_DIMESION + "]的适配文件");
			} else if (args.length >= 1) {
				addition = args[0];
				System.out.println("以默认分辨率[" + baseW + "*" + baseH + "]为基准，生成默认分辨率[" + addition + "]的适配文件");
			}
		} catch (NumberFormatException e) {

			System.err.println("right input params : java -jar xxx.jar width height w,h_w,h_..._w,h;");
			e.printStackTrace();
			System.exit(-1);
		}

		new GenerateValueFiles(baseW, baseH, addition).generate();
	}

	/** 生成xml中节点的格式{0}-HEIGHT {1}-WIDTH*/
	private final static String WTemplate = "<dimen name=\"px{0}\">{1}px</dimen>\n";

	/** 生成的文件夹格式{0}-HEIGHT {1}-WIDTH*/
	private final static String VALUE_TEMPLATE = "values-{0}x{1}";

	/** 默认所支持的分辨率*/
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

		// 将当前要生成的分辨率，添加到默认分辨率中
		if (!this.supportStr.contains(baseX + "," + baseY)) {
			this.supportStr += baseX + "," + baseY + ";";
		}

		// 获取当前所有支持的分辨率
		this.supportStr += validateInput(supportStr);

		System.out.println(supportStr);

		// 当前目录下创建res文件夹
		File dir = new File(dirStr);
		if (!dir.exists()) {
			dir.mkdir();

		}
		System.out.println(dir.getAbsoluteFile());

	}

	/**
	 * 解析所有支持的分辨率
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
	 * 生成适配文件
	 */
	public void generate() {
		String[] vals = supportStr.split(";");
		for (String val : vals) {
			String[] wh = val.split(",");
			generateXmlFile(Integer.parseInt(wh[0]), Integer.parseInt(wh[1]));
		}
	}

	/**
	 * 生成xml文件
	 * @param w
	 * @param h
	 */
	private void generateXmlFile(int w, int h) {

		// 准备xml头，root标签信息
		StringBuffer sbForWidth = new StringBuffer();
		sbForWidth.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		sbForWidth.append("<resources>\n");

		// 计算以基准width为标准计算每1基准所对应的当前像素大小，
		// 如 480 / 720 = 0.66 当前屏幕宽度为480像素，分720份，每一份为0.66像素
		float cellw = w * 1.0f / baseW;

		System.out.println("width : " + w + "," + baseW + "," + cellw);

		// 生成所有的适配对应值（以基准高度为数量生成）
		for (int i = 1; i <= baseH; i++) {
			sbForWidth.append(WTemplate.replace("{0}", i + "").replace("{1}", change(cellw * i) + ""));
		}

		// xml文件结尾
		sbForWidth.append("</resources>");

		// 生成values-高度x宽度的文件夹
		File fileDir = new File(dirStr + File.separator + VALUE_TEMPLATE.replace("{0}", h + "").replace("{1}", w + ""));
		fileDir.mkdir();
		
		//生成xml文件
		File layxFile = new File(fileDir.getAbsolutePath(), "dimens.xml");
		try {
			//讲内容写入xml文件
			PrintWriter pw = new PrintWriter(new FileOutputStream(layxFile));
			pw.print(sbForWidth.toString());
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	//格式转换
	public static float change(float a) {
		int temp = (int) (a * 100);
		return temp / 100f;
	}

}