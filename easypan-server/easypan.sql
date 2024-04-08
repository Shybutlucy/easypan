/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 80100 (8.1.0)
 Source Host           : localhost:3306
 Source Schema         : easypan

 Target Server Type    : MySQL
 Target Server Version : 80100 (8.1.0)
 File Encoding         : 65001

 Date: 08/04/2024 15:21:58
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for email_code
-- ----------------------------
DROP TABLE IF EXISTS `email_code`;
CREATE TABLE `email_code` (
  `email` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '邮箱',
  `code` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '验证码',
  `status` tinyint(1) DEFAULT NULL COMMENT '0:未使用 1:已使用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`email`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of email_code
-- ----------------------------
BEGIN;
INSERT INTO `email_code` (`email`, `code`, `status`, `create_time`) VALUES ('2627311935@qq.com', '07662', 1, '2023-05-17 17:31:21');
INSERT INTO `email_code` (`email`, `code`, `status`, `create_time`) VALUES ('2627311935@qq.com', '18624', 1, NULL);
INSERT INTO `email_code` (`email`, `code`, `status`, `create_time`) VALUES ('2627311935@qq.com', '18740', 1, NULL);
INSERT INTO `email_code` (`email`, `code`, `status`, `create_time`) VALUES ('2627311935@qq.com', '38614', 1, '2023-05-17 17:34:15');
INSERT INTO `email_code` (`email`, `code`, `status`, `create_time`) VALUES ('2627311935@qq.com', '39451', 1, NULL);
INSERT INTO `email_code` (`email`, `code`, `status`, `create_time`) VALUES ('2627311935@qq.com', '52443', 1, NULL);
INSERT INTO `email_code` (`email`, `code`, `status`, `create_time`) VALUES ('2627311935@qq.com', '70266', 1, '2023-05-17 16:30:13');
INSERT INTO `email_code` (`email`, `code`, `status`, `create_time`) VALUES ('2627311935@qq.com', '73964', 1, NULL);
INSERT INTO `email_code` (`email`, `code`, `status`, `create_time`) VALUES ('2627311935@qq.com', '78583', 1, NULL);
INSERT INTO `email_code` (`email`, `code`, `status`, `create_time`) VALUES ('2627311935@qq.com', '80746', 1, '2023-06-04 14:44:19');
INSERT INTO `email_code` (`email`, `code`, `status`, `create_time`) VALUES ('2627311935@qq.com', '99851', 1, '2023-05-27 13:29:19');
INSERT INTO `email_code` (`email`, `code`, `status`, `create_time`) VALUES ('test@qq.test', '53570', 0, '2023-05-26 22:50:56');
COMMIT;

-- ----------------------------
-- Table structure for file_info
-- ----------------------------
DROP TABLE IF EXISTS `file_info`;
CREATE TABLE `file_info` (
  `id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件ID',
  `user_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `file_md5` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件MD5值',
  `file_pid` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件父ID',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小',
  `filename` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件名',
  `file_cover` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件封面',
  `file_path` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件路径',
  `folder_type` tinyint(1) DEFAULT NULL COMMENT '0文件 1目录',
  `file_category` tinyint(1) DEFAULT NULL COMMENT '文件分类 1:视频 2:音频 3:图片 4:文档 5:其他',
  `file_type` tinyint(1) DEFAULT NULL COMMENT '文件分类 1:视频 2:音频 3:图片 4:pdf 5:doc 6:excel 7:txt 8:代码 9:压缩包 10:其他',
  `status` tinyint(1) DEFAULT NULL COMMENT '0:转码中 1:转码失败 2:转码成功',
  `recovery_time` datetime DEFAULT NULL COMMENT '进入回收站时间',
  `version` int DEFAULT '1' COMMENT '乐观锁',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '0:正常 1:回收站 2:删除',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`,`user_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_md5` (`file_md5`),
  KEY `idx_file_pid` (`file_pid`),
  KEY `idx_deleted` (`deleted`),
  KEY `idx_revover_time` (`recovery_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文件信息表';

-- ----------------------------
-- Records of file_info
-- ----------------------------
BEGIN;
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('2cJd8bpEzU', '3494146119', '9fe1f3f9666c8492b3ae2ac5933111da', 'QFdfm3D9VS', 12542, 'Login.vue', NULL, '202306/34941461192cJd8bpEzU.vue', 0, 4, 8, 2, NULL, 1, 0, '2023-06-11 11:43:25', '2023-06-11 11:43:25');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('65cmvJ7vCM', '3494146119', 'e38157881d1223f2c981c6dcf4b87bce', 'JG2cnf5shs', 474609074, '电锯人第一集.HD1080P.X264.AAC.mp4', '202306/349414611965cmvJ7vCM.png', '202306/349414611965cmvJ7vCM.mp4', 0, 1, 1, 2, NULL, 1, 0, '2023-06-07 19:50:45', '2023-06-07 19:50:48');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('6xCRaxx5Am', '3494146119', NULL, '0', NULL, '图片', NULL, NULL, 1, NULL, NULL, 2, NULL, 1, 0, '2023-06-11 22:17:57', '2023-06-11 22:17:57');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('9ZwjbOzCPN', '3494146119', '20807d7e46952e3dcf02c8d0d82e5921', 'awA4FaVY6b', 36329101, '《白日梦想家》预告片2.flv', '202306/34941461199ZwjbOzCPN.png', '202306/34941461199ZwjbOzCPN.flv', 0, 1, 1, 2, NULL, 1, 0, '2023-06-08 22:01:05', '2023-06-08 22:01:06');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('awA4FaVY6b', '3494146119', NULL, '0', NULL, '视频', NULL, NULL, 1, NULL, NULL, 2, '2023-07-14 14:59:45', 1, 0, '2023-06-05 15:32:43', '2023-07-14 14:59:55');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('bZ7Z8p9coI', '3494146119', NULL, 'awA4FaVY6b', NULL, '你的名字', NULL, NULL, 1, NULL, NULL, 2, NULL, 1, 0, '2023-06-05 19:01:15', '2023-06-06 22:06:16');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('CAma4sUZdE', '3494146119', 'f81788fa8f133d0520e69821f1c0c171', 'ZT43iuKELw', 3020065, '匈牙利旅游.docx', NULL, '202306/3494146119CAma4sUZdE.docx', 0, 4, 5, 2, NULL, 1, 0, '2023-06-10 17:44:52', '2023-06-11 22:15:05');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('Cwo2QlSPNk', '3494146119', '67e8083bcb5b2f73f8e290e33231ae23', 'ZT43iuKELw', 15642, '激光雷达卫星探测.docx', NULL, '202306/3494146119Cwo2QlSPNk.docx', 0, 4, 5, 2, NULL, 1, 0, '2023-06-10 17:06:15', '2023-06-11 22:15:05');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('D4DtYQkqWT', '3494146119', '51d1ce35130693af7fbd9ad69a885a47', '0', 8871264, 'natapp', NULL, '202306/3494146119D4DtYQkqWT', 0, 5, 10, 2, NULL, 1, 0, '2023-06-12 16:03:46', '2023-06-12 16:03:46');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('d815YduMlU', '3494146119', '7cdc40f4d0c0e9b2641fb02bae8e94e5', '0', 1154706, '【作业】人工智能和三维地质建模的结合-1202221584-宋吴星.pptx', NULL, '202306/3494146119d815YduMlU.pptx', 0, 5, 10, 2, '2023-07-14 14:58:26', 1, 1, '2023-06-07 20:02:44', '2023-07-14 14:56:58');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('dkFlIombJp', '3494146119', '89ad8e61015637003d8370e85013cc95', 'QFdfm3D9VS', 2385, 'pom.xml', NULL, '202306/3494146119dkFlIombJp.xml', 0, 4, 8, 2, NULL, 1, 0, '2023-06-11 22:21:15', '2023-06-12 16:20:37');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('dmUlePZDqa', '3494146119', NULL, 'bZ7Z8p9coI', NULL, '你的名字', NULL, '202306/3494146119YFrQc6AOs4.mp4', 0, 1, 1, 2, '2024-04-08 14:57:26', 1, 1, '2023-06-05 19:01:01', '2023-06-05 19:01:01');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('dmUlePZDqb', '3494146119', NULL, 'awA4FaVY6b', NULL, '小丑', NULL, NULL, 1, NULL, NULL, 2, NULL, 1, 0, '2023-06-05 19:01:01', '2023-06-05 19:01:01');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('F3F0loDr91', '3494146119', '9c296b4aeab411f2bd01ec6853421ead', 'ZT43iuKELw', 1907439, '基于多源卫星测高数据的洞庭湖流域2003—2017年湖泊水位变化监测.pdf', NULL, '202306/3494146119F3F0loDr91.pdf', 0, 4, 4, 2, NULL, 1, 0, '2023-06-04 15:51:55', '2023-06-11 22:15:05');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('f6UzfnfK7F', '3494146119', NULL, '0', NULL, '音乐', NULL, NULL, 1, NULL, NULL, 2, '2023-07-14 14:59:21', 1, 0, '2023-06-28 22:07:04', '2023-07-14 14:59:27');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('fHjZSCaucS', '3494146119', '973564adacbf0157cebe07d820bfbd71', '6xCRaxx5Am', 213643, 'avatar.jpeg', '202306/3494146119fHjZSCaucS_.jpeg', '202306/3494146119fHjZSCaucS.jpeg', 0, 3, 3, 2, NULL, 1, 0, '2023-06-08 14:09:58', '2023-06-11 22:18:04');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('GTsRgNh2Mf', '3494146119', 'b7a1b1157bdb5f9d021159368eb4a17f', '0', 52290409, '单光子光子应用.zip', NULL, '202306/3494146119GTsRgNh2Mf.zip', 0, 5, 9, 2, '2023-07-14 14:58:26', 1, 1, '2023-06-07 20:00:27', '2023-07-14 14:56:58');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('Hcg8xJAWad', '3494146119', NULL, '0', NULL, '电锯人', NULL, NULL, 1, NULL, NULL, 2, '2023-07-14 15:00:34', 1, 1, '2023-07-14 15:00:25', '2023-07-14 15:00:25');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('IEhVwcw0hg', '3494146119', 'cee0dd524e6db68e7602a3dfb1ecf9de', 'ZT43iuKELw', 2308722, '机载激光测深技术及其研究进展.pdf', NULL, '202306/3494146119IEhVwcw0hg.pdf', 0, 4, 4, 2, NULL, 1, 0, '2023-06-04 15:40:30', '2023-06-11 22:15:05');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('inHujZOenR', '3494146119', '04fdfd86a83ba024369aa514f9b95295', 'QFdfm3D9VS', 199350, 'guigu-oa.sql', NULL, '202306/3494146119inHujZOenR.sql', 0, 4, 8, 2, NULL, 1, 0, '2023-06-11 22:24:39', '2023-06-12 16:20:37');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('IoDvjYeieJ', '3494146119', 'e6c7ef69af08e31706fc8c8df439b379', 'f6UzfnfK7F', 9310047, '林宥嘉 - 热血无赖.mp3', NULL, '202306/3494146119IoDvjYeieJ.mp3', 0, 2, 2, 2, NULL, 1, 0, '2023-06-11 23:51:04', '2023-06-28 22:07:10');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('JG2cnf5shs', '3494146119', NULL, 'awA4FaVY6b', NULL, '电锯人', NULL, NULL, 1, NULL, NULL, 2, '2023-07-14 15:00:18', 1, 0, '2023-06-07 19:49:03', '2023-07-14 15:00:48');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('kePANfVadk', '3494146119', '5ed5ca203bdcc04e4f8b3c53074113f0', 'bZ7Z8p9coI', 56516103, 'sparkle_your_name_am720p.mp4', '202404/3494146119kePANfVadk.png', '202404/3494146119kePANfVadk.mp4', 0, 1, 1, 2, NULL, 1, 0, '2024-04-08 14:57:53', '2024-04-08 14:57:54');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('M9SYdo0EFQ', '3494146119', '41e72ca88f9c720ca8aebd7550295f05', 'JG2cnf5shs', 16504, '项目布局.md', NULL, '202306/3494146119M9SYdo0EFQ.md', 0, 4, 8, 2, '2023-06-08 19:52:00', 1, 0, '2023-06-07 19:59:53', '2023-07-14 14:13:47');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('mAzL64IfSu', '3494146119', '0f45f154c29c8d26a7ccd1dfe8546b30', 'ZT43iuKELw', 19782, '学生答辩问题.xlsx', NULL, '202306/3494146119mAzL64IfSu.xlsx', 0, 4, 6, 2, NULL, 1, 0, '2023-06-04 15:35:08', '2023-06-11 22:15:05');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('MsHEIEflRR', '3494146119', 'cde6d370b4a7ebe01f48100906cea7f5', '0', 10350885, 'OCC.zip', NULL, '202306/3494146119MsHEIEflRR.zip', 0, 5, 9, 2, '2023-06-28 22:00:39', 1, 0, '2023-06-12 15:32:06', '2023-07-14 14:28:06');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('ndmZktHrWJ', '3494146119', '0efb7c36567acc2b7b37a124c7f175b7', 'QFdfm3D9VS', 330, 'index.html', NULL, '202306/3494146119ndmZktHrWJ.html', 0, 4, 8, 2, NULL, 1, 0, '2023-06-11 11:17:05', '2023-06-11 11:17:05');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('pC1ZO8KdaC', '3494146119', '0a31f553a7ee1d02a8fa098d048366b0', 'ZT43iuKELw', 807424, '匈牙利文化概述.doc', NULL, '202306/3494146119pC1ZO8KdaC.doc', 0, 4, 5, 2, NULL, 1, 0, '2023-06-04 15:22:47', '2023-06-11 22:15:05');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('QFdfm3D9VS', '3494146119', NULL, '0', NULL, '代码', NULL, NULL, 1, NULL, NULL, 2, NULL, 1, 0, '2023-06-11 11:16:52', '2023-06-11 11:16:52');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('w0BhdLMOuh', '3494146119', 'f5776675911f8d671b74bc754e10ffb3', 'ZT43iuKELw', 9654157, '基于3D激光雷达的无人水面艇海上目标检测.pdf', NULL, '202306/3494146119w0BhdLMOuh.pdf', 0, 4, 4, 2, NULL, 1, 0, '2023-06-04 16:07:02', '2023-06-11 22:15:05');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('xD2vDAE4Nq', '3494146119', 'd1a871e99620c641936c2a43c58efe5d', 'QFdfm3D9VS', 933, 'test.txt', NULL, '202306/3494146119xD2vDAE4Nq.txt', 0, 4, 7, 2, NULL, 1, 0, '2023-06-11 22:19:02', '2023-06-12 16:20:37');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('YFrQc6AOs3', '3494146119', '9de4d07018cdb28c2747b63e37cc2bbb', 'ZT43iuKELw', 14285, '函数peakWidth.docx', NULL, '202306/3494146119YFrQc6AOs3.docx', 0, 4, 5, 2, NULL, 1, 0, '2023-06-10 16:44:07', '2023-06-11 22:15:05');
INSERT INTO `file_info` (`id`, `user_id`, `file_md5`, `file_pid`, `file_size`, `filename`, `file_cover`, `file_path`, `folder_type`, `file_category`, `file_type`, `status`, `recovery_time`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('ZT43iuKELw', '3494146119', NULL, '0', NULL, '文档', NULL, NULL, 1, NULL, NULL, 2, NULL, 1, 0, '2023-06-11 22:14:41', '2023-06-11 22:14:41');
COMMIT;

-- ----------------------------
-- Table structure for file_share
-- ----------------------------
DROP TABLE IF EXISTS `file_share`;
CREATE TABLE `file_share` (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '分享ID',
  `file_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件ID',
  `user_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户ID',
  `valid_type` tinyint(1) DEFAULT NULL COMMENT '有效期类型 0:1天 1:7天 2:30天 3:永久有效',
  `code` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '提取码',
  `browse_count` int DEFAULT '0' COMMENT '浏览次数',
  `save_count` int DEFAULT '0' COMMENT '保存次数',
  `download_count` int DEFAULT '0' COMMENT '下载次数',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  `version` int DEFAULT '1' COMMENT '乐观锁',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of file_share
-- ----------------------------
BEGIN;
INSERT INTO `file_share` (`id`, `file_id`, `user_id`, `valid_type`, `code`, `browse_count`, `save_count`, `download_count`, `deleted`, `version`, `expire_time`, `create_time`, `update_time`) VALUES ('IZkmij16ULTenM1Eajly', 'MsHEIEflRR', '3494146119', 1, 'luHPM', 0, 0, 0, 0, 1, '2023-07-21 16:27:37', '2023-07-14 16:27:37', '2023-07-14 16:27:37');
INSERT INTO `file_share` (`id`, `file_id`, `user_id`, `valid_type`, `code`, `browse_count`, `save_count`, `download_count`, `deleted`, `version`, `expire_time`, `create_time`, `update_time`) VALUES ('JpY0JGNFTHoSAkxn3VWZ', 'D4DtYQkqWT', '3494146119', 3, 'Ia9T', 0, 0, 0, 0, 1, NULL, '2023-07-14 16:28:45', '2023-07-14 16:28:45');
COMMIT;

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `nickname` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '昵称',
  `email` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
  `qq_open_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'QQ openID',
  `qq_avatar` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'QQ头像',
  `password` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后一次登陆时间',
  `use_space` bigint DEFAULT NULL COMMENT '使用空间 单位 byte',
  `total_space` bigint DEFAULT NULL COMMENT '总空间',
  `status` tinyint(1) DEFAULT '1' COMMENT '用户状态，0:禁用 1:启用',
  `version` int DEFAULT '1' COMMENT '乐观锁',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除 0:正常 1:删除',
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_email` (`email`) USING BTREE,
  UNIQUE KEY `qq_open_id` (`qq_open_id`) USING BTREE,
  UNIQUE KEY `key_nickname` (`nickname`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户信息';

-- ----------------------------
-- Records of user_info
-- ----------------------------
BEGIN;
INSERT INTO `user_info` (`id`, `nickname`, `email`, `qq_open_id`, `qq_avatar`, `password`, `last_login_time`, `use_space`, `total_space`, `status`, `version`, `deleted`, `create_time`, `update_time`) VALUES ('3494146119', 'swcode', '2627311935@qq.com', NULL, NULL, '5a1c646dd5e3c9bb4fbdf61ea4c99f4b', NULL, 667624792, 1073741824, 1, 1, 0, '2024-04-08 14:57:52', '2024-04-08 14:57:52');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
