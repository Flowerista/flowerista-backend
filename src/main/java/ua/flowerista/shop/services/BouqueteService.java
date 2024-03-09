package ua.flowerista.shop.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.flowerista.shop.dto.BouqueteCardDto;
import ua.flowerista.shop.dto.BouqueteDto;
import ua.flowerista.shop.dto.BouqueteSmallDto;
import ua.flowerista.shop.dto.PriceRangeDto;
import ua.flowerista.shop.mappers.BouqueteMapper;
import ua.flowerista.shop.models.Bouquete;
import ua.flowerista.shop.models.BouqueteSize;
import ua.flowerista.shop.models.Size;
import ua.flowerista.shop.repo.BouqueteRepository;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BouqueteService {

	private BouqueteRepository repo;
	private BouqueteMapper mapper;
	private CloudinaryService cloudinary;

	@Autowired
	public BouqueteService(BouqueteRepository repo, BouqueteMapper mapper, CloudinaryService cloudinary) {
		this.repo = repo;
		this.mapper = mapper;
		this.cloudinary = cloudinary;
	}

	public void insert(BouqueteDto dto, List<MultipartFile> images) {
		Map<Integer, String> imageUrls = new HashMap<>();
		for (int i = 0; i < images.size(); i++) {
			 MultipartFile image = images.get(i);
			 String imageUrl = null;
			 try {
				imageUrl = cloudinary.uploadImage(image);
			} catch (IOException e) {
				e.printStackTrace();
			}
			 imageUrls.put(++i, imageUrl);
		}
		dto.setImageUrls(imageUrls);
		repo.save(mapper.toEntity(dto));
	}

	public void insert(BouqueteDto dto) {
		repo.save(mapper.toEntity(dto));
	}

	public void deleteById(int id) {
		repo.deleteById(id);
	}

	public List<BouqueteDto> getAllBouquetes() {
		return repo.findAll().stream().map(bouquete -> mapper.toDto(bouquete)).collect(Collectors.toList());
	}

	public BouqueteDto getBouqueteById(int id) {
		return mapper.toDto(repo.getReferenceById(id));
	}

	public void update(BouqueteDto dto) {
		repo.save(mapper.toEntity(dto));
	}

	public List<BouqueteSmallDto> getBouquetesBestSellers() {
		return repo.findTop5ByOrderBySoldQuantityDesc().stream().map(bouquete -> mapper.toSmallDto(bouquete))
				.collect(Collectors.toList());
	}

	public List<BouqueteSmallDto> getBouquetesTop5Sales() {
		return repo.findTop5ByOrderByDiscountDesc().stream().map(bouquete -> mapper.toSmallDto(bouquete))
				.collect(Collectors.toList());
	}

	public Page<BouqueteSmallDto> getBouquetesCatalogFiltered (List<Integer> flowerIds, List<Integer> colorIds, Integer minPrice, Integer maxPrice, Boolean sortByNewest,Boolean sortByPriceHighToLow,Boolean sortByPriceLowToHigh, int page) {
		Pageable pageable = PageRequest.of(page - 1, 20);
		return repo.findByFilters(flowerIds, colorIds, minPrice, maxPrice, sortByNewest, sortByPriceHighToLow, sortByPriceLowToHigh, pageable).map(mapper::toSmallDto);

	}

    public PriceRangeDto getMinMaxPrices() {
        Integer minPrice = repo.findMinPrice();
        Integer maxPrice = repo.findMaxPrice();

        return new PriceRangeDto(minPrice, maxPrice);
    }

    public BouqueteCardDto getById (int id) {
    	return mapper.toCardDto(repo.getReferenceById(id));
    }

    public List<BouqueteSmallDto> searchBouquetesByName(String name) {
        if (name == null || name.length() < 3) {
            return Collections.emptyList();
        }
        return repo.searchByName(name).stream().map(boquete -> mapper.toSmallDto(boquete)).collect(Collectors.toList());
    }

	public Boolean isBouqueteExist(Integer id) {
		return repo.existsById(id);
	}

    public void test () {
    	Bouquete bouquete = new Bouquete();
    	BouqueteSize sizeS = new BouqueteSize();
    	sizeS.setSize(Size.SMALL);
    	sizeS.setDefaultPrice(BigInteger.valueOf(123));
    	BouqueteSize sizeM = new BouqueteSize();
    	sizeM.setSize(Size.MEDIUM);
    	sizeM.setDefaultPrice(BigInteger.valueOf(140));
    	BouqueteSize sizeL = new BouqueteSize();
    	sizeL.setSize(Size.LARGE);
    	sizeL.setDefaultPrice(BigInteger.valueOf(180));

    	Set<BouqueteSize> sizes = new HashSet<>();
    	sizes.add(sizeS);
    	sizes.add(sizeM);
    	sizes.add(sizeL);
    	bouquete.setSizes(sizes);
    	bouquete.setName("123");
    	bouquete.setItemCode("BQ051");
    	bouquete.setQuantity(12);
    	bouquete.setSoldQuantity(13);
    	repo.save(bouquete);
    }

	public boolean isBouqueteAvailableForSale(Integer productId) {
		return repo.isBouqueteAvailableForSale(productId);
	}
}
