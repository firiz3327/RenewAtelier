package net.firiz.renewatelier.inventory.delivery

import net.firiz.renewatelier.alchemy.material.AlchemyIngredients
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial
import net.firiz.renewatelier.characteristic.Characteristic

class DeliveryObject(
        val material: AlchemyMaterial,
        val reqAmount: Int,
        val reqQuality: Int,
        val reqCharacteristic: List<Characteristic> = arrayListOf(),
        val reqIngredients: List<AlchemyIngredients> = arrayListOf()
)