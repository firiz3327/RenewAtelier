package jp.gr.java_conf.zakuramomiji.renewatelier.inventory.delivery

import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.AlchemyIngredients
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.AlchemyMaterial
import jp.gr.java_conf.zakuramomiji.renewatelier.characteristic.Characteristic

class DeliveryObject(
        val material: AlchemyMaterial,
        val reqAmount: Int,
        val reqQuality: Int,
        val reqCharacteristic: List<Characteristic> = arrayListOf(),
        val reqIngredients: List<AlchemyIngredients> = arrayListOf()
)