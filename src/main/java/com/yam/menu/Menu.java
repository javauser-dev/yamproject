package com.yam.menu;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.multipart.MultipartFile;

import com.yam.shop.Shop;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "yam_menu")
@SequenceGenerator(name = "yam_menu_generator", sequenceName = "yam_menu_seq", initialValue = 1, allocationSize = 1)
public class Menu {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "yam_menu_generator")
	private Long no;

	@Column(length = 50, nullable = false)
	private String category;

	@Column(length = 10, nullable = false)
	private String name;

	@Column(length = 20, nullable = false)
	private String simpleExp;

	@Column
	@Builder.Default
	private String filename = "";

	@Transient
	private MultipartFile image;

	@Column(nullable = false)
	private String price;

	@CreationTimestamp
	@ColumnDefault(value = "sysdate")
	private LocalDateTime createdAt;

	@CreationTimestamp
	@ColumnDefault(value = "sysdate")
	private LocalDateTime updatedAt;

	@Column(length = 70, nullable = false)
	private String component;

	// Menu.java에 추가
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shop_no")
	private Shop shop;

	// Menu.java에 추가
	public void setShop(Shop shop) {
		this.shop = shop;
		// 매장의 메뉴 목록에도 현재 메뉴 추가
		if (shop != null && !shop.getMenus().contains(this)) {
			shop.getMenus().add(this);
		}
	}
}