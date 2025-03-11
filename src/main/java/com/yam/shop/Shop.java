package com.yam.shop;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yam.menu.Menu;
import com.yam.store.Store;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name="yam_shop")
@SequenceGenerator(
		name = "yam_shop_generator",
		sequenceName = "yam_shop_seq",
		initialValue = 1,
		allocationSize = 1)
public class Shop {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="yam_shop_generator")
	private Long shopNo;
	
	@Column(length=30, unique = true, nullable = false) // ✅ 중복 방지
    private String shopBusinessNumber;
	
	@Column(length=20, nullable= false)
	private String storeName;
	
	@Column(nullable = false)
	private boolean enabled = false;
	
	@Column(length=10)
	private String shopCategory;

	@Column(length=20, nullable= false)
	private String shopName;
	
	@Column(length=70)
	private String shopInfo;

	@Column
	@Builder.Default
	private String filename = "";
	
	@Transient
	private MultipartFile shopMainImage;
	
	@Transient
	private MultipartFile shopMenuImage;
	
	@Column(length=30)
	private String shopShortDesc;
	
	
	@Column(length = 1000)
	private String shopLongDesc;
	
	@Column(length=20)
	private String shopSignatureMenu;
	
	@Column(length=10, nullable= false)
	private String shopPhone;
	
	@Column(length=70, nullable= false)
	private String shopAddress;
	
	@Column(length=30)
	private String shopWebsite;
	
	@CreationTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate shopSubDate;
	
	@Column(length=30)
	private String shopOpentime;
	
	@Column(length=30)
	private String shopClosetime;
	
	@Column(length=70)
	private String shopFacilities;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_no")
    private Store store;  // Store와 연결
	
	// Shop.java에 추가
	@OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Menu> menus = new ArrayList<>();
	
	// Shop.java에 추가
	public void addMenu(Menu menu) {
	    this.menus.add(menu);
	    // 메뉴의 매장 참조도 설정
	    if (menu.getShop() != this) {
	        menu.setShop(this);
	    }
	}
}
